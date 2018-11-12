package com.xuhao.didi.socket.server.impl;

import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.socket.common.interfaces.basic.AbsLoopThread;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManagerPrivate;
import com.xuhao.didi.socket.server.action.IAction;
import com.xuhao.didi.socket.server.exceptions.IllegalAccessException;
import com.xuhao.didi.socket.server.exceptions.InitiativeDisconnectException;
import com.xuhao.didi.socket.server.impl.clientpojo.ClientImpl;
import com.xuhao.didi.socket.server.impl.clientpojo.ClientPoolImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManagerImpl extends AbsServerRegisterProxy implements IServerManagerPrivate<OkServerOptions> {

    private boolean isInit = false;

    private int mServerPort = -999;

    private ServerSocket mServerSocket;

    private ClientPoolImpl mClientPoolImpl;

    private OkServerOptions mServerOptions;

    private AbsLoopThread mAcceptThread;

    @Override
    public void initServerPrivate(int serverPort) {
        checkCallStack();
        if (!isInit && mServerPort == -999) {
            init(this);

            mServerPort = serverPort;
            mServerActionDispatcher.setServerPort(mServerPort);
            isInit = true;
            SLog.w("server manager initiation");
        } else {
            SLog.e("duplicate init server manager!");
        }
    }

    private void checkCallStack() {
        StackTraceElement[] elementsArray = Thread.currentThread().getStackTrace();
        boolean isValid = false;
        for (StackTraceElement e : elementsArray) {
            if (e.getClassName().contains("ManagerHolder") && e.getMethodName().equals("getServer")) {
                isValid = true;
            }
        }
        if (!isValid) {
            throw new IllegalAccessException("You can't call this method directly.This is privately function! ");
        }
    }

    @Override
    public void listen() {
        if (mServerOptions == null) {
            mServerOptions = OkServerOptions.getDefault();
        }
        listen(mServerOptions);
    }

    @Override
    public void listen(OkServerOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("option can not be null");
        }
        if (!(options instanceof OkServerOptions)) {
            throw new IllegalArgumentException("option must instanceof OkServerOptions");
        }
        try {
            mServerOptions = options;
            mServerSocket = new ServerSocket(mServerPort);
            configuration(mServerSocket);
            mAcceptThread = new AcceptThread("server accepting in " + mServerPort);
            mAcceptThread.start();
        } catch (Exception e) {
            shutdown();
        }
    }

    @Override
    public boolean isLive() {
        return isInit && mServerSocket != null
                && !mServerSocket.isClosed()
                && mAcceptThread != null
                && !mAcceptThread.isShutdown();
    }

    @Override
    public IClientPool<String, IClient> getClientPool() {
        return (IClientPool)mClientPoolImpl;
    }

    private class AcceptThread extends AbsLoopThread {

        public AcceptThread(String name) {
            super(name);
        }

        @Override
        protected void beforeLoop() throws Exception {
            mClientPoolImpl = new ClientPoolImpl(mServerOptions.getConnectCapacity());
            mServerActionDispatcher.setClientPool(mClientPoolImpl);
            sendBroadcast(IAction.Server.ACTION_SERVER_LISTENING);
        }

        @Override
        protected void runInLoopThread() throws Exception {
            Socket socket = mServerSocket.accept();
            ClientImpl client = new ClientImpl(socket, mServerOptions);
            client.setClientPool(mClientPoolImpl);
            client.setServerStateSender(ServerManagerImpl.this);
            client.startIOEngine();
        }

        @Override
        protected void loopFinish(Exception e) {
            if (!(e instanceof InitiativeDisconnectException)) {
                sendBroadcast(IAction.Server.ACTION_SERVER_WILL_BE_SHUTDOWN, e);
            }
        }
    }


    private void configuration(ServerSocket serverSocket) {
        //TODO 待细化配置
    }

    @Override
    public void shutdown() {
        if (mServerSocket == null) {
            return;
        }

        if (mClientPoolImpl != null) {
            mClientPoolImpl.serverDown();
        }

        try {
            mServerSocket.close();
        } catch (IOException e) {
        }

        mServerSocket = null;
        mClientPoolImpl = null;
        mAcceptThread.shutdown(new InitiativeDisconnectException());
        mAcceptThread = null;

        sendBroadcast(IAction.Server.ACTION_SERVER_ALLREADY_SHUTDOWN);
    }

}
