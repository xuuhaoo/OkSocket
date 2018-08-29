package com.xuhao.android.common.interfacies.client.io;

import com.xuhao.android.common.interfacies.client.msg.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IWriter<T> {
    boolean write() throws RuntimeException;

    void setOption(T option);

    void offer(ISendable sendable);

    void close();

}
