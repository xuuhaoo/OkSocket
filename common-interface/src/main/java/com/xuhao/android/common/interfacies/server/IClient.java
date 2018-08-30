package com.xuhao.android.common.interfacies.server;

import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.client.IDisConnectable;
import com.xuhao.android.common.interfacies.client.msg.ISender;

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
