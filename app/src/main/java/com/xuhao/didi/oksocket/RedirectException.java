package com.xuhao.didi.oksocket;

import com.xuhao.didi.libsocket.sdk.client.ConnectionInfo;

/**
 * Created by xuhao on 2017/6/30.
 */

public class RedirectException extends RuntimeException {
    public ConnectionInfo redirectInfo;

    public RedirectException(ConnectionInfo redirectInfo) {
        this.redirectInfo = redirectInfo;
    }
}
