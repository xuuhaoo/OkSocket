package com.xuhao.android.common.interfacies;

public interface IServerManager extends IRegister {
    void initLocalPortPrivate(int localPort);

    void listen();

    void shutdown();
}
