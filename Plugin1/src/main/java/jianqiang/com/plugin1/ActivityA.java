package jianqiang.com.plugin1;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.jianqiang.mypluginlibrary.ZeusBaseActivity;

public class ActivityA extends ZeusBaseActivity {
    private final static String TAG = "ActivityA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        String username = getIntent().getStringExtra("UserName");
        TextView tv = (TextView)findViewById(R.id.tv);
        tv.setText(username);

        findViewById(R.id.btnGotoActivityA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();

                    String activityName = "jianqiang.com.plugin1.ActivityA";
                    intent.setComponent(new ComponentName("jianqiang.com.plugin1", activityName));
                    intent.putExtra("UserName", "baobao");

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnGotoTestActivity1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                String activityName = "jianqiang.com.plugin1.TestActivity1";
                intent.setComponent(new ComponentName("jianqiang.com.plugin1", activityName));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra("UserName");
        TextView tv = (TextView)findViewById(R.id.tv);
        tv.setText(username);
    }
}
