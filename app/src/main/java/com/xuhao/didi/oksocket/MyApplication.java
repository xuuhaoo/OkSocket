package com.xuhao.didi.oksocket;

import android.app.Application;

import com.xuhao.didi.libsocket.sdk.OkSocket;


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
