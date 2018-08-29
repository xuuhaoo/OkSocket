package com.xuhao.android.common.utils;

import android.util.Log;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SPIUtils {

    public static <E> E load(Class<E> clz) {
        if (clz == null) {
            Log.e("Loader", "load null clz error!");
            return null;
        }
        ServiceLoader<E> serviceLoader = ServiceLoader.load(clz, clz.getClassLoader());
        Iterator<E> it = serviceLoader.iterator();
        try {
            if (it.hasNext()) {
                E service = it.next();
                return service;
            }
        } catch (Throwable throwable) {
            Log.e("Loader", "load " + clz.getSimpleName() + " error! " + throwable.getMessage());
        }
        return null;
    }
}
