package com.xuhao.android.server.impl;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IServerActionListener;
import com.xuhao.android.server.action.ServerActionDispatcher;
import com.xuhao.android.server.impl.client.ClientPool;

import java.io.Serializable;

public class AbsServerRegister implements IRegister<IServerActionListener>, IStateSender {

    protected ServerActionDispatcher mServerActionDispatcher;

    public AbsServerRegister(Context context) {
        mServerActionDispatcher = new ServerActionDispatcher(context);
    }

    @Override
    public void registerReceiver(BroadcastReceiver broadcastReceiver, String... action) {
        mServerActionDispatcher.registerReceiver(broadcastReceiver, action);
    }

    @Override
    public void registerReceiver(IServerActionListener socketActionListener) {
        mServerActionDispatcher.registerReceiver(socketActionListener);
    }

    @Override
    public void unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        mServerActionDispatcher.unRegisterReceiver(broadcastReceiver);
    }

    @Override
    public void unRegisterReceiver(IServerActionListener socketActionListener) {
        mServerActionDispatcher.unRegisterReceiver(socketActionListener);
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        mServerActionDispatcher.sendBroadcast(action, serializable);
    }

    @Override
    public void sendBroadcast(String action) {
        mServerActionDispatcher.sendBroadcast(action);
    }
}
