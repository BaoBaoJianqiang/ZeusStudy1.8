package com.example.jianqiang.mypluginlibrary;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

public class PluginManager {
    public final static List<PluginItem> plugins = new ArrayList<PluginItem>();

    //正在使用的Resources
    public static volatile Resources mNowResources;

    //原始的application中的BaseContext，不能是其他的，否则会内存泄漏
    public static volatile Context mBaseContext;

    //ContextImpl中的LoadedAPK对象mPackageInfo
    private static Object mPackageInfo = null;

    public static volatile ClassLoader mNowClassLoader = null;          //系统原始的ClassLoader
    public static volatile ClassLoader mBaseClassLoader = null;         //系统原始的ClassLoader

    public static void init(Application application) {
        //初始化一些成员变量和加载已安装的插件
        mPackageInfo = RefInvoke.getFieldObject(application.getBaseContext(), "mPackageInfo");
        mBaseContext = application.getBaseContext();
        mNowResources = mBaseContext.getResources();

        mBaseClassLoader = mBaseContext.getClassLoader();
        mNowClassLoader = mBaseContext.getClassLoader();

        try {
            AssetManager assetManager = application.getAssets();
            String[] paths = assetManager.list("");

            ArrayList<String> pluginPaths = new ArrayList<String>();
            for(String path : paths) {
                if(path.endsWith(".apk")) {
                    String apkName = path;

                    Utils.extractAssets(mBaseContext, apkName);

                    PluginItem item = generatePluginItem(apkName);
                    plugins.add(item);

                    pluginPaths.add(item.pluginPath);
                }
            }

            reloadInstalledPluginResources(pluginPaths);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ZeusClassLoader classLoader = new ZeusClassLoader(mBaseContext.getPackageCodePath(), mBaseContext.getClassLoader());

        File dexOutputDir = mBaseContext.getDir("dex", Context.MODE_PRIVATE);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        for(PluginItem plugin: plugins) {
            DexClassLoader dexClassLoader = new DexClassLoader(plugin.pluginPath,
                    dexOutputPath, null, mBaseClassLoader);
            classLoader.addPluginClassLoader(dexClassLoader);
        }

        RefInvoke.setFieldObject(mPackageInfo, "mClassLoader", classLoader);
        Thread.currentThread().setContextClassLoader(classLoader);
        mNowClassLoader = classLoader;

    }

    private static PluginItem generatePluginItem(String apkName) {
        File file = mBaseContext.getFileStreamPath(apkName);
        PluginItem item = new PluginItem();
        item.pluginPath = file.getAbsolutePath();
        item.packageInfo = DLUtils.getPackageInfo(mBaseContext, item.pluginPath);
        item.applicationName = ApplicationHelper.loadApplication(mBaseContext, file);

        return item;
    }

    private static void reloadInstalledPluginResources(ArrayList<String> pluginPaths) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);

            addAssetPath.invoke(assetManager, mBaseContext.getPackageResourcePath());

            for(String pluginPath: pluginPaths) {
                addAssetPath.invoke(assetManager, pluginPath);
            }

            Resources newResources = new Resources(assetManager,
                    mBaseContext.getResources().getDisplayMetrics(),
                    mBaseContext.getResources().getConfiguration());

            RefInvoke.setFieldObject(mBaseContext, "mResources", newResources);
            //这是最主要的需要替换的，如果不支持插件运行时更新，只留这一个就可以了
            RefInvoke.setFieldObject(mPackageInfo, "mResources", newResources);

            //清除一下之前的resource的数据，释放一些内存
            //因为这个resource有可能还被系统持有着，内存都没被释放
            //clearResoucesDrawableCache(mNowResources);

            mNowResources = newResources;
            //需要清理mtheme对象，否则通过inflate方式加载资源会报错
            //如果是activity动态加载插件，则需要把activity的mTheme对象也设置为null
            RefInvoke.setFieldObject(mBaseContext, "mTheme", null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
