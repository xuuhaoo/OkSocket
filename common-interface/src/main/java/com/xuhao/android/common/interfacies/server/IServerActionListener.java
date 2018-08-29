package com.xuhao.android.common.interfacies.server;

import android.content.Context;

public interface IServerActionListener {
    void onServerListening(Context context, int serverPort);

    void onClientConnected(Context context, IClient client, int serverPort, IClientPool clientPool);

    void onClientDisconnected(Context context, IClient client, int serverPort, IClientPool clientPool);

    void onServerWillBeShutdown(Context context, int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable);

    void onServerAlreadyShutdown(Context context, int serverPort);

}
