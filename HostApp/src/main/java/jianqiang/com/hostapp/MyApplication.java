package jianqiang.com.hostapp;

import android.app.Application;
import android.content.Context;

import com.example.jianqiang.mypluginlibrary.PluginItem;
import com.example.jianqiang.mypluginlibrary.PluginManager;

import java.util.HashMap;

import jianqiang.com.hostapp.ams_hook.AMSHookHelper;

public class MyApplication extends Application {

    public static HashMap<String, String> pluginActivies = new HashMap<String, String>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        PluginManager.init(this);

        //get json data from server
        mockData();

        try {
            AMSHookHelper.hookAMN();
            AMSHookHelper.attachContext();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    void mockData() {
        pluginActivies.put("jianqiang.com.plugin1.ActivityA", "jianqiang.com.hostapp.SingleTopActivity1");
        pluginActivies.put("jianqiang.com.plugin1.TestActivity1", "jianqiang.com.hostapp.SingleTaskActivity2");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        for(PluginItem pluginItem: PluginManager.plugins) {

            try {
                Class clazz = PluginManager.mNowClassLoader.loadClass(pluginItem.applicationName);
                Application application = (Application)clazz.newInstance();

                if(application == null)
                    continue;

                application.onCreate();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
