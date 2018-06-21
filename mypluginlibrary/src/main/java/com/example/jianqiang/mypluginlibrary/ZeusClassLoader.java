package com.example.jianqiang.mypluginlibrary;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import static java.lang.System.arraycopy;

/***
 * 这是一个空ClassLoader，主要是个容器
 * <p>
 * Created by huangjian on 2016/6/21.
 */
class ZeusClassLoader extends PathClassLoader {
    private List<DexClassLoader> mClassLoaderList = null;

    public ZeusClassLoader(String dexPath, ClassLoader parent) {
        super(dexPath, parent);

        mClassLoaderList = new ArrayList<DexClassLoader>();
    }

    /**
     * 添加一个插件到当前的classLoader中
     */
    protected void addPluginClassLoader(DexClassLoader dexClassLoader) {
        mClassLoaderList.add(dexClassLoader);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            //先查找parent classLoader，这里实际就是系统帮我们创建的classLoader，目标对应为宿主apk
            clazz = getParent().loadClass(className);
        } catch (ClassNotFoundException ignored) {

        }

        if (clazz != null) {
            return clazz;
        }

        //挨个的到插件里进行查找
        if (mClassLoaderList != null) {
            for (DexClassLoader classLoader : mClassLoaderList) {
                if (classLoader == null) continue;
                try {
                    //这里只查找插件它自己的apk，不需要查parent，避免多次无用查询，提高性能
                    clazz = classLoader.loadClass(className);
                    if (clazz != null) {
                        return clazz;
                    }
                } catch (ClassNotFoundException ignored) {

                }
            }
        }
        throw new ClassNotFoundException(className + " in loader " + this);
    }
}

