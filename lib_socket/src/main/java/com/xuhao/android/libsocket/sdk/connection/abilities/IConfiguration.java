package com.xuhao.android.libsocket.sdk.connection.abilities;

import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IConfiguration {
    IConnectionManager option(OkSocketOptions okOptions);

    OkSocketOptions getOption();
}
