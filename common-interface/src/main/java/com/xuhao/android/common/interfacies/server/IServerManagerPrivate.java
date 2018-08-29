package com.xuhao.android.common.interfacies.server;

import android.content.Context;

import com.xuhao.android.common.interfacies.IIOCoreOptions;
import com.xuhao.android.common.interfacies.dispatcher.IRegister;

public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(Context context, int serverPort);
}
