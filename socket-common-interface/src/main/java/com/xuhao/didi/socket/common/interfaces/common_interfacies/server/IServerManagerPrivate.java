package com.xuhao.didi.socket.common.interfaces.common_interfacies.server;


import com.xuhao.didi.core.iocore.interfaces.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(int serverPort);
}
