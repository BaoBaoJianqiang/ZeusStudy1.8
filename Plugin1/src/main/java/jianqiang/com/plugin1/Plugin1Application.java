package jianqiang.com.plugin1;

import android.app.Application;

public class Plugin1Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        userName = "jianqiang.bao";
    }

    public static String userName;
}
