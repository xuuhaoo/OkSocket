package com.xuhao.android.oksocket;

import android.app.Application;

import com.xuhao.android.libsocket.sdk.OkSocket;


/**
 * Created by xuhao on 2017/5/22.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkSocket.initialize(this, true);
    }
}
