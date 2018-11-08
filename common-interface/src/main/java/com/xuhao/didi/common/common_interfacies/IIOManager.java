package com.xuhao.didi.common.common_interfacies;

import com.xuhao.didi.common.common_interfacies.client.msg.ISendable;

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
