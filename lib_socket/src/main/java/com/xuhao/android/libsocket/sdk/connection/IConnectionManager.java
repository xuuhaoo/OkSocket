package com.xuhao.android.libsocket.sdk.connection;

import com.xuhao.android.libsocket.impl.PulseManager;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.connection.abilities.IConfiguration;
import com.xuhao.android.libsocket.sdk.connection.abilities.IConnectable;
import com.xuhao.android.libsocket.sdk.connection.abilities.IDisConnectable;
import com.xuhao.android.libsocket.sdk.connection.abilities.IRegister;
import com.xuhao.android.libsocket.sdk.connection.abilities.ISender;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IConnectionManager extends
        IConfiguration,
        IConnectable,
        IDisConnectable,
        ISender<IConnectionManager>,
        IRegister {

    boolean isConnect();

    PulseManager getPulseManager();

    void setIsConnectionHolder(boolean isHold);

    ConnectionInfo getConnectionInfo();

    void switchConnectionInfo(ConnectionInfo info);

    AbsReconnectionManager getReconnectionManager();

}

