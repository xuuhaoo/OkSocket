package com.xuhao.android.libsocket.sdk.connection;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.xuhao.android.libsocket.impl.PulseManager;
import com.xuhao.android.libsocket.interfaces.IPulseSendable;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.interfacies.ISocketActionListener;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuhao on 2017/6/5.
 */

public abstract class AbsReconnectionManager implements ISocketActionListener {

    protected Context mContext;

    protected IConnectionManager mConnectionManager;

    protected PulseManager mPulseManager;

    protected volatile Set<Class<? extends Exception>> mIgnoreDisconnectExceptionList = new LinkedHashSet<>();

    public AbsReconnectionManager() {

    }

    @CallSuper
    public void attach(Context context, IConnectionManager manager) {
        detach();
        mContext = context.getApplicationContext();
        mConnectionManager = manager;
        mPulseManager = manager.getPulseManager();
        mConnectionManager.registerReceiver(this);
    }

    @CallSuper
    public void detach() {
        if (mConnectionManager != null) {
            mConnectionManager.unRegisterReceiver(this);
        }
    }

    public void addIgnoreException(Class<? extends Exception> e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.add(e);
        }
    }

    public void removeIgnoreException(Exception e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.remove(e.getClass());
        }
    }

    public void removeIgnoreException(Class<? extends Exception> e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.remove(e);
        }
    }

    public void removeAll() {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.clear();
        }
    }

    @Override
    public void onSocketIOThreadStart(Context context, String action) {
        //do nothing;
    }

    @Override
    public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
        //do nothing;
    }

    @Override
    public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
        //do nothing;
    }

    @Override
    public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
        //do nothing;
    }

    @Override
    public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
        //do nothing;
    }

}
