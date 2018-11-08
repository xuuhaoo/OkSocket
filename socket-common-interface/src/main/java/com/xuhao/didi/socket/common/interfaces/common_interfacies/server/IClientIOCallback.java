package com.xuhao.didi.socket.common.interfaces.common_interfacies.server;


import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
