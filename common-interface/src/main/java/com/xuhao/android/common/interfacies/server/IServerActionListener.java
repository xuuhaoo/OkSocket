package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.IClientPool;

public interface IServerActionListener {
    void onServerListenerSuccess(int localPort);

    void onServerListenerFailed(int localPort, Throwable throwable);

    void onClientConnected(IClient client, int serverLocalPort, IClientPool clientPool);

    void onClientDisconnected(IClient client, int serverLocalPort, IClientPool clientPool);

    void onServerWillBeShutdown(IClientPool clientPool);

}
