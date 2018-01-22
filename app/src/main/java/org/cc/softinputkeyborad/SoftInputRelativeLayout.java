package org.cc.softinputkeyborad;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * 此 View 可修复 Activity 的 adjustResize 无效问题<br>
 * 当全屏(沉浸式)主题状态下 使用 adjustResize 属性时,弹出键盘布局并不会被压缩,
 * 这个 bug 可以用一个 fix 方法修复,但是兼容性不好,部分手机的状态栏高度无法处理.<br>
 * 使用方法: 需要压缩的内容只要用这个 View 包裹即可.<br>
 * 通过该 View 还能回调监听键盘的展开/收缩状态,和高度<br>
 * Created by siyehua on 2017/10/23.
 */
public class SoftInputRelativeLayout extends RelativeLayout {
    /**
     * 键盘监听
     */
    public interface Listener {
        /**
         * @param flag   true: 展开, false: 收缩
         * @param height 布局改变的高度(键盘展开时,等于键盘高度,键盘收缩时,等于0)
         */
        void change(boolean flag, int height);
    }

    private Listener listener;
    private boolean showKeyBordFlag = false;
    private int contentHeight;//布局高度
    private int changeHeight;//布局改变的高度

    public SoftInputRelativeLayout(Context context) {
        super(context);
        init();
    }

    public SoftInputRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SoftInputRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SoftInputRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 隐藏键盘监听
     */
    private MessageQueue.IdleHandler hideHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            //隐藏键盘,且界面的重新绘制已经完成

            //通知监听器键盘的状态
            listener.change(false, changeHeight);

            //一秒后移除监听
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    Looper.myQueue().removeIdleHandler(hideHandler);
                }
            }, 1000);
            return false;
        }
    };

    /**
     * 显示键盘监听
     */
    private MessageQueue.IdleHandler showHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            //显示键盘,且界面的重新绘制已经完成

            //通知监听器键盘的状态
            listener.change(true, changeHeight);

            //一秒后移除监听
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    Looper.myQueue().removeIdleHandler(showHandler);
                }
            }, 1000);
            return false;
        }
    };

    private void init() {
//        setFitsSystemWindows(true);

        //获取 View 的高度
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (contentHeight == 0) {
                    Rect r = new Rect();
                    getWindowVisibleDisplayFrame(r);
                    contentHeight = r.bottom - r.top;
                }
                return true;
            }
        });

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (contentHeight == 0 || listener == null) {
                    return;
                }
                Rect r = new Rect();
                getWindowVisibleDisplayFrame(r);
                //当前可见高度
                int visible = r.bottom - r.top;
                //变化高度
                changeHeight = contentHeight - visible;
                if (changeHeight == 0 && showKeyBordFlag) {
                    //键盘收缩,布局无变化不能证明键盘收缩,还需 showKeyBordFlag 表明原来处于展开状态
                    showKeyBordFlag = false;
                    Looper.myQueue().addIdleHandler(hideHandler);
                } else if (changeHeight != 0 && !showKeyBordFlag) {
                    //键盘展开,布局有变化,且当前不处于展开状态
                    showKeyBordFlag = true;
                    Looper.myQueue().addIdleHandler(showHandler);
                }
            }
        });
    }


    /**
     * 设置键盘弹出收起监听
     *
     * @param listener 监听器 {@link Listener}
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
