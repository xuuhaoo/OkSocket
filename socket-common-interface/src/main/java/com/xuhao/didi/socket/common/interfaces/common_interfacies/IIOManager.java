package com.xuhao.didi.socket.common.interfaces.common_interfacies;


import com.xuhao.didi.core.iocore.interfaces.IIOCoreOptions;
import com.xuhao.didi.core.iocore.interfaces.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IIOManager<E extends IIOCoreOptions> {
    void startEngine();

    void setOkOptions(E options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
