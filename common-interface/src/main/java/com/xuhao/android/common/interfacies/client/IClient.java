package com.xuhao.android.common.interfacies.client;

import com.xuhao.android.common.interfacies.client.msg.ISender;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getIp();

    String getTag();

    void setReaderProtocol();

}
