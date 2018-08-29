package com.xuhao.android.common.interfacies.client.io;

import com.xuhao.android.common.interfacies.IIOCoreOptions;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;

import java.io.OutputStream;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IWriter<T extends IIOCoreOptions> {

    void initialize(OutputStream outputStream, IStateSender stateSender);

    boolean write() throws RuntimeException;

    void setOption(T option);

    void offer(ISendable sendable);

    void close();

}
