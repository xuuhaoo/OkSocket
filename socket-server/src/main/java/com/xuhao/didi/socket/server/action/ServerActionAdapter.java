package com.xuhao.didi.socket.server.action;


import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;

public abstract class ServerActionAdapter implements IServerActionListener {
    @Override
    public void onServerListening(int serverPort) {

    }

    @Override
    public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {

    }

    @Override
    public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {

    }

    @Override
    public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {

    }

    @Override
    public void onServerAlreadyShutdown(int serverPort) {

    }
}
