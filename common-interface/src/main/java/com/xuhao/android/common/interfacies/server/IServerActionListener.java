package com.xuhao.android.common.interfacies.server;

import android.content.Context;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.IClientPool;

public interface IServerActionListener {
    void onServerListenSuccess(Context context, int localPort);

    void onServerListenFailed(Context context, int localPort, Throwable throwable);

    void onClientConnected(Context context, IClient client, int serverLocalPort, IClientPool clientPool);

    void onClientDisconnected(Context context, IClient client, int serverLocalPort, IClientPool clientPool);

    void onServerWillBeShutdown(Context context, int localPort, IClientPool clientPool);

    void onServerAllreadyShutdown(Context context, int localPort, Throwable throwable);

}
