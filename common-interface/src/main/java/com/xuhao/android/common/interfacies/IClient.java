package com.xuhao.android.common.interfacies;

public interface IClient extends IDisConnectable, ISender<IClient> {

    String getIp();

    String getTag();

    void setReaderProtocol();

}
