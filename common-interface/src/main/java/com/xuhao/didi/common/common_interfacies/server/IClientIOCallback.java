package com.xuhao.didi.common.common_interfacies.server;

import com.xuhao.didi.common.basic.bean.OriginalData;
import com.xuhao.didi.common.common_interfacies.client.msg.ISendable;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
