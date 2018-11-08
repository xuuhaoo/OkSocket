package com.xuhao.didi.socket.client.sdk.client;


import java.net.Socket;

public abstract class OkSocketFactory {

    public abstract Socket createSocket(ConnectionInfo info, OkSocketOptions options) throws Exception;

}
