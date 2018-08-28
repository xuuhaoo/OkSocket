package com.xuhao.android.libserver.impl;

import android.content.BroadcastReceiver;

import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.server.IServerActionListener;

public class AbsServerRegister implements IRegister<IServerActionListener> {

    @Override
    public void registerReceiver(BroadcastReceiver broadcastReceiver, String... action) {

    }

    @Override
    public void registerReceiver(IServerActionListener socketActionListener) {

    }

    @Override
    public void unRegisterReceiver(BroadcastReceiver broadcastReceiver) {

    }

    @Override
    public void unRegisterReceiver(IServerActionListener socketActionListener) {

    }

}
