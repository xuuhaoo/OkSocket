package com.xuhao.android.common.interfacies.server;

import android.content.Context;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.IClientPool;

public interface IServerActionListener {
    void onServerListenSuccess(Context context, int serverPort);

    void onServerListenFailed(Context context, int serverPort, Throwable throwable);

    void onClientConnected(Context context, IClient client, int serverPort, IClientPool clientPool);

    void onClientDisconnected(Context context, IClient client, int serverPort, IClientPool clientPool);

    void onServerWillBeShutdown(Context context, int serverPort, IClientPool clientPool);

    void onServerAllreadyShutdown(Context context, int serverPort, Throwable throwable);

}
