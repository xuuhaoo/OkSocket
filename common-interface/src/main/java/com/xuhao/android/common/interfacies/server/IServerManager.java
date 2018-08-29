package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.interfacies.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

}
