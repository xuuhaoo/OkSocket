package com.xuhao.android.libsocket.impl.client.abilities;

import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IIOManager {
    void resolve();

    void setOkOptions(OkSocketOptions options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
