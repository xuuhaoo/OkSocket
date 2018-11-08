package com.xuhao.didi.common.common_interfacies.server;

import android.content.Context;

import com.xuhao.didi.common.common_interfacies.IIOCoreOptions;

public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(Context context, int serverPort);
}
