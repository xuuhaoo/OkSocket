package com.xuhao.android.libsocket.sdk;

import android.content.Context;

import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.interfacies.ISocketActionListener;

/**
 * Socket行为适配器,是行为监听器的一个Simple版本,详情请见{@link ISocketActionListener}
 * Created by xuhao on 2017/5/17.
 */

public abstract class SocketActionAdapter implements ISocketActionListener {

    @Override
    public void onSocketIOThreadStart(Context context, String action) {

    }

    @Override
    public void onSocketIOThreadShutdown(Context context, String action, Exception e) {

    }

    @Override
    public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {

    }

    @Override
    public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {

    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {

    }

    @Override
    public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {

    }

    @Override
    public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {

    }

    @Override
    public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {

    }
}
