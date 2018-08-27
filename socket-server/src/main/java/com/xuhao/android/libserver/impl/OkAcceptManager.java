package com.xuhao.android.libserver.impl;

import android.util.Log;

import com.xuhao.android.spi.interfacies.IAcceptManager;

public class OkAcceptManager implements IAcceptManager {
    @Override
    public void acceptOn(int port) {
        Log.i("OkAcceptManager", "acceptOn:" + port);
    }

    @Override
    public void close() {

    }
}
