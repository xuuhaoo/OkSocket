package com.xuhao.android.common.interfacies.client;

import com.xuhao.android.common.interfacies.client.msg.ISender;

public interface IClient extends IDisConnectable, ISender<IClient> {

    String getIp();

    String getTag();

    void setReaderProtocol();

}
