package com.xuhao.android.libsocket.impl.abilities;

import com.xuhao.android.libsocket.sdk.OkSocketOptions;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IReader {

    void read() throws RuntimeException;

    void setOption(OkSocketOptions option);
}
