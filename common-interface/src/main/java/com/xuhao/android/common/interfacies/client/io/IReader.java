package com.xuhao.android.common.interfacies.client.io;


import com.xuhao.android.common.interfacies.IIOCoreOptions;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;

import java.io.InputStream;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IReader<T extends IIOCoreOptions> {

    void initialize(InputStream inputStream, IStateSender stateSender);

    void read() throws RuntimeException;

    void setOption(T option);

    void close();
}
