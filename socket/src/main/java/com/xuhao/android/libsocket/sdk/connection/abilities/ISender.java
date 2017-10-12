package com.xuhao.android.libsocket.sdk.connection.abilities;

import com.xuhao.android.libsocket.sdk.bean.ISendable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface ISender<T> {
    T send(ISendable sendable);
}
