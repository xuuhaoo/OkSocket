package com.xuhao.didi.common.common_interfacies.server;

import com.xuhao.didi.common.common_interfacies.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

}
