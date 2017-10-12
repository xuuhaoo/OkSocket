package com.xuhao.android.libsocket.impl.abilities;

import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IWriter {
    boolean write() throws RuntimeException;

    void setOption(OkSocketOptions option);

    void offer(ISendable sendable);

    int queueSize();

}
