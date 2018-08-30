package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.client.msg.ISendable;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
