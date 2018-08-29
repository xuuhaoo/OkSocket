package com.xuhao.android.server.impl;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IServerActionListener;
import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.server.action.ServerActionDispatcher;

import java.io.Serializable;

public class AbsServerRegisterProxy implements IRegister<IServerActionListener, IServerManager>, IStateSender {

    protected ServerActionDispatcher mServerActionDispatcher;

    private IServerManager<OkServerOptions> mManager;

    protected void init(Context context, IServerManager<OkServerOptions> serverManager) {
        mManager = serverManager;
        mServerActionDispatcher = new ServerActionDispatcher(context, mManager);
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(BroadcastReceiver broadcastReceiver, String... action) {
        return mServerActionDispatcher.registerReceiver(broadcastReceiver, action);
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.registerReceiver(socketActionListener);
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        return mServerActionDispatcher.unRegisterReceiver(broadcastReceiver);
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.unRegisterReceiver(socketActionListener);
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
