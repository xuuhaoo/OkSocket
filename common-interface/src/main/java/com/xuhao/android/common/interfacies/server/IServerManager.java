package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.interfacies.dispatcher.IRegister;

public interface IServerManager extends IRegister<IServerActionListener> {
    void initServerPortPrivate(int localPort);

    void listen();

    void shutdown();
}
