package com.xuhao.android.server.impl;

import android.content.Context;

import com.xuhao.android.common.basic.AbsLoopThread;
import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.server.impl.client.ClientPool;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerManagerImpl extends AbsServerRegister implements IServerManager {

    private int mServerPort = -999;

    private ServerSocket mServerSocket;

    private ClientPool mClientPool;

    private OkServerOptions mServerOptions;

    private AbsLoopThread mAcceptThread;

    private Context mContext;

    public ServerManagerImpl(Context context, OkServerOptions options) {
        super(context);
        mContext = context;
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

    private class AcceptThread extends AbsLoopThread {

        public AcceptThread(Context context, String name) {
            super(context, name);
        }

        @Override
        protected void beforeLoop() throws Exception {
            super.beforeLoop();
        }

        @Override
        protected void runInLoopThread() throws Exception {
            mServerSocket.accept();
        }

        @Override
        protected void loopFinish(Exception e) {

        }
    }

    @Override
    public void listen() {
        try {
            mServerSocket = new ServerSocket(mServerPort);
            mAcceptThread = new AcceptThread(mContext, "server accepting in " + mServerPort);
            mAcceptThread.start();

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
