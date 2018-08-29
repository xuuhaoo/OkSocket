package com.xuhao.android.server.impl;

import android.content.Context;

import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.server.impl.client.ClientPool;

import java.io.IOException;
import java.net.ServerSocket;

public class OkServerManagerImpl extends AbsServerRegister implements IServerManager {

    private int mServerPort = -999;

    private ServerSocket mServerSocket;

    private ClientPool mClientPool;

    private OkServerOptions mServerOptions;

    public OkServerManagerImpl(Context context, OkServerOptions options) {
        super(context);
        mServerOptions = options;
        mClientPool = new ClientPool(mServerOptions.getConnectCapcity());
        mServerActionDispatcher.setClientPool(mClientPool);
    }

    @Override
    public void initServerPortPrivate(int serverPort) {
        if (mServerPort == -999) {
            mServerPort = serverPort;
            mServerActionDispatcher.setServerPort(mServerPort);
        } else {
            throw new IllegalStateException("You can't call this method directly.Should call OkSocket.server(" + serverPort + ")");
        }
    }

    @Override
    public void listen() {
        try {
            mServerSocket = new ServerSocket(mServerPort);
            mServerSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        if (mServerSocket == null) {
            return;
        }

        mServerSocket = null;

    }

}
