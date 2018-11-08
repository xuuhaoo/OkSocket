package com.xuhao.didi.core.iocore.interfaces;


import com.xuhao.didi.common.common_interfacies.IIOCoreOptions;
import com.xuhao.didi.common.common_interfacies.dispatcher.IStateSender;

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
