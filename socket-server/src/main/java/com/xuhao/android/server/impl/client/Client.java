package com.xuhao.android.server.impl.client;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.msg.ISendable;

public class Client implements IClient {
    @Override
    public String getIp() {
        return null;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void setReaderProtocol() {

    }

    @Override
    public void disconnect(Exception e) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public IClient send(ISendable sendable) {
        return null;
    }
}
