package com.xuhao.didi.socket.server.impl;


import com.xuhao.didi.core.iocore.interfaces.IStateSender;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.server.action.ServerActionDispatcher;

import java.io.Serializable;

public class AbsServerRegisterProxy implements IRegister<IServerActionListener, IServerManager>, IStateSender {

    protected ServerActionDispatcher mServerActionDispatcher;

    private IServerManager<OkServerOptions> mManager;

    protected void init(IServerManager<OkServerOptions> serverManager) {
        mManager = serverManager;
        mServerActionDispatcher = new ServerActionDispatcher(mManager);
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(IServerActionListener socketActionListener) {
        return mServerActionDispatcher.registerReceiver(socketActionListener);
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
