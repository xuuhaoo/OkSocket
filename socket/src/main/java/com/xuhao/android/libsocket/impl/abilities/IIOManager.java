package com.xuhao.android.libsocket.impl.abilities;

import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IIOManager {
    void resolve();

    void setOkOptions(OkSocketOptions options);

    void send(ISendable sendable);

    void close();

}
