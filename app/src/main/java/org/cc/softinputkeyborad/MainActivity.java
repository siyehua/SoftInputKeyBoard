package org.cc.softinputkeyborad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 需要显示的 view
        final View showView = findViewById(R.id.v_add);
        SoftInputRelativeLayout softInputRelativeLayout = findViewById(R.id.rl_content);
        softInputRelativeLayout.setListener(new SoftInputRelativeLayout.Listener() {
            @Override
            public void change(boolean flag, int height) {
                Log.e("keyboard", "flag: " + flag + "   height: " + height);
                Toast.makeText(MainActivity.this, "KeyBoard is show: " + flag, Toast.LENGTH_SHORT).show();
                if (flag) {
                    showView.setVisibility(View.VISIBLE);
                    //如果不想显出出来的 view 挡住了 editText ,此时可以动态调整布局
                    //例如此时动态设置 editText 的 margin, 或设置 外部的滚动view(LitView,ScrollView 等) 的滚动位置
                } else {
                    showView.setVisibility(View.GONE);
                    //同理如在显示view 的时候调整了布局,此时要重新恢复布局设置
                }
            }
        });
    }
}
