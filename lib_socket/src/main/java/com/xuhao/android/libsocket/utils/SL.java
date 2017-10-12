package com.xuhao.android.libsocket.utils;

import android.util.Log;

import com.xuhao.android.libsocket.sdk.OkSocketOptions;

/**
 * Created by xuhao on 2017/6/9.
 */

public class SL {

    public static void e(String msg) {
        if (OkSocketOptions.isDebug()) {
            Log.e("Socket", msg);
        }
    }

    public static void i(String msg) {
        if (OkSocketOptions.isDebug()) {
            Log.i("Socket", msg);
        }
    }

    public static void w(String msg) {
        if (OkSocketOptions.isDebug()) {
            Log.w("Socket", msg);
        }
    }
}
