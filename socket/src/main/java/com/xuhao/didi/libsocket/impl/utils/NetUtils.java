package com.xuhao.didi.libsocket.impl.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by didi on 2018/4/19.
 */

public class NetUtils {
    /**
     * 网络是否可用
     *
     * @param context 上下文
     * @return true则网络可用, false则网络不可用
     */
    public static boolean netIsAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return !(networkinfo == null || !networkinfo.isAvailable());
    }

}
