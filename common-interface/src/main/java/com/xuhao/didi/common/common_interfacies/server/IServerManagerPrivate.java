package com.xuhao.didi.common.common_interfacies.server;

import android.content.Context;

import com.xuhao.didi.core.iocore.interfaces.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(Context context, int serverPort);
}
