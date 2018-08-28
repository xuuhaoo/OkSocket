package com.xuhao.android.libserver.impl;

import com.xuhao.android.common.interfacies.IServerManager;

import java.io.IOException;
import java.net.ServerSocket;

public class OkServerManagerImpl extends AbsServerRegister implements IServerManager {
    private int mLocalPort = -999;

    private ServerSocket mServerSocket;

    public OkServerManagerImpl() {
        try {
            mServerSocket = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initLocalPortPrivate(int localPort) {
        if (mLocalPort == -999) {
            mLocalPort = localPort;
        } else {
            throw new IllegalStateException("You can't call this method directly.Should call OkSocket.server(" + localPort + ")");
        }
    }

    @Override
    public void listen() {
        if (mServerSocket == null) {
            return;
        }

    }

    @Override
    public void shutdown() {
        if (mServerSocket == null) {
            return;
        }

    }

}
