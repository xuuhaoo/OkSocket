package com.xuhao.android.common.interfacies;

public interface ISocketActionListener {
    void onServerListenerSuccess(int localPort);

    void onServerListenerFailed(int localPort, Throwable throwable);

    void onClientConnected(IClient client, int serverLocalPort, IClientPool clientPool);

    void onClientDisconnected(IClient client, int serverLocalPort, IClientPool clientPool);

    void onServerWillBeShutdown(IClientPool clientPool);

}
