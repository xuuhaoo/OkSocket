package com.xuhao.didi.socket.client.impl.client;


import com.xuhao.didi.socket.client.impl.client.abilities.IConnectionSwitchListener;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.Serializable;


/**
 * Created by xuhao on 2017/5/17.
 */

public abstract class AbsConnectionManager implements IConnectionManager {
    /**
     * 连接信息
     */
    protected ConnectionInfo mConnectionInfo;
    /**
     * 连接信息switch监听器
     */
    private IConnectionSwitchListener mConnectionSwitchListener;
    /**
     * 状态机
     */
    protected ActionDispatcher mActionDispatcher;

    public AbsConnectionManager(ConnectionInfo info) {
        mConnectionInfo = info;
        mActionDispatcher = new ActionDispatcher(info, this);
    }

    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
        mActionDispatcher.registerReceiver(socketResponseHandler);
        return this;
    }

    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        mActionDispatcher.unRegisterReceiver(socketResponseHandler);
        return this;
    }

    protected void sendBroadcast(String action, Serializable serializable) {
        mActionDispatcher.sendBroadcast(action, serializable);
    }

    protected void sendBroadcast(String action) {
        mActionDispatcher.sendBroadcast(action);
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        if (mConnectionInfo != null) {
            return mConnectionInfo.clone();
        }
        return null;
    }

    @Override
    public void switchConnectionInfo(ConnectionInfo info) {
        if (info != null) {
            ConnectionInfo tempOldInfo = mConnectionInfo;
            mConnectionInfo = info.clone();
            if (mActionDispatcher != null) {
                mActionDispatcher.setConnectionInfo(mConnectionInfo);
            }
            if (mConnectionSwitchListener != null) {
                mConnectionSwitchListener.onSwitchConnectionInfo(this, tempOldInfo, mConnectionInfo);
            }
        }
    }

    protected void setOnConnectionSwitchListener(IConnectionSwitchListener listener) {
        mConnectionSwitchListener = listener;
    }
}
