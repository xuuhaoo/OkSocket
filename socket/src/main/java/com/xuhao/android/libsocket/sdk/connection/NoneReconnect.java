package com.xuhao.android.libsocket.sdk.connection;

import android.content.Context;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;

/**
 * Created by Tony on 2017/10/24.
 */

public class NoneReconnect extends AbsReconnectionManager {
    @Override
    public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {

    }

    @Override
    public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {

    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {

    }
}
