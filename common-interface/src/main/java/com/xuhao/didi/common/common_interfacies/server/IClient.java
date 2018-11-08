package com.xuhao.didi.common.common_interfacies.server;

import com.xuhao.didi.common.common_interfacies.IReaderProtocol;
import com.xuhao.didi.common.common_interfacies.client.IDisConnectable;
import com.xuhao.didi.common.common_interfacies.client.ISender;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    void setUniqueTag(String uniqueTag);

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
