package jianqiang.com.plugin1;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jianqiang.mypluginlibrary.PluginManager;
import com.example.jianqiang.mypluginlibrary.ZeusBaseActivity;

public class TestActivity1 extends ZeusBaseActivity {
    private final static String TAG = "TestActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        Application application = getApplication();
        Context context = getApplicationContext();
        ApplicationInfo applicationInfo = getApplicationInfo();

        Button btnGotoActivityA = (Button)findViewById(R.id.btnGotoActivityA);
        btnGotoActivityA.setText(Plugin1Application.userName);
        btnGotoActivityA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();

                    String activityName = "jianqiang.com.plugin1.ActivityA";
                    intent.setComponent(new ComponentName("jianqiang.com.plugin1", activityName));
                    intent.putExtra("UserName", "jianqiang");

                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
