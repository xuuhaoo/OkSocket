package com.xuhao.android.common.interfacies.client.io;


/**
 * Created by xuhao on 2017/5/16.
 */

public interface IReader<T> {

    void read() throws RuntimeException;

    void setOption(T option);

    void close();
}
