package com.xuhao.didi.socket.common.interfaces.common_interfacies.server;


public interface IServerActionListener {
    void onServerListening(int serverPort);

    void onClientConnected(IClient client, int serverPort, IClientPool clientPool);

    void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool);

    void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable);

    void onServerAlreadyShutdown(int serverPort);

}
