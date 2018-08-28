package com.xuhao.android.server.impl;

import android.content.BroadcastReceiver;

import com.xuhao.android.common.interfacies.client.msg.ISender;
import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IServerActionListener;
import com.xuhao.android.server.action.ServerActionDispatcher;

import java.io.Serializable;

public class AbsServerRegister implements IRegister<IServerActionListener> ,IStateSender{

    protected ServerActionDispatcher mServerActionDispatcher;

    public AbsServerRegister() {
        mServerActionDispatcher = new ServerActionDispatcher()
    }

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

    @Override
    public void sendBroadcast(String action, Serializable serializable) {

    }

    @Override
    public void sendBroadcast(String action) {

    }
}
