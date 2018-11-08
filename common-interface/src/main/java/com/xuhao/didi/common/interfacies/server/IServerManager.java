package com.xuhao.didi.common.interfacies.server;

import com.xuhao.didi.common.interfacies.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

}
