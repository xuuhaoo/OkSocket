package com.xuhao.android.server.impl;

import com.xuhao.android.common.interfacies.server.IServerManager;

import java.io.IOException;
import java.net.ServerSocket;

public class OkServerManagerImpl extends AbsServerRegister implements IServerManager {

    private int mServerPort = -999;

    private ServerSocket mServerSocket;

    public OkServerManagerImpl() {

    }

    @Override
    public void initServerPortPrivate(int serverPort) {
        if (mServerPort == -999) {
            mServerPort = serverPort;
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
