package com.xuhao.didi.libsocket.sdk.client.connection;

import android.content.Context;
import android.support.annotation.CallSuper;

import com.xuhao.didi.common.basic.bean.OriginalData;
import com.xuhao.didi.common.common_interfacies.client.msg.ISendable;
import com.xuhao.didi.libsocket.impl.client.PulseManager;
import com.xuhao.didi.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.didi.libsocket.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 抽象联调管理器
 * Created by xuhao on 2017/6/5.
 */
public abstract class AbsReconnectionManager implements ISocketActionListener {
    /**
     * 上下文
     */
    protected Context mContext;
    /**
     * 连接管理器
     */
    protected IConnectionManager mConnectionManager;
    /**
     * 心跳管理器
     */
    protected PulseManager mPulseManager;
    /**
     * 是否销毁
     */
    protected volatile boolean mDetach;
    /**
     * 需要忽略的断开连接集合,当Exception在此集合中,忽略该类型的断开异常,不会自动重连
     */
    protected volatile Set<Class<? extends Exception>> mIgnoreDisconnectExceptionList = new LinkedHashSet<>();

    public AbsReconnectionManager() {

    }

    /**
     * 关联到某一个连接管理器
     *
     * @param context 上下文
     * @param manager 当前连接管理器
     */
    @CallSuper
    public void attach(Context context, IConnectionManager manager) {
        detach();
        mDetach = false;
        mContext = context.getApplicationContext();
        mConnectionManager = manager;
        mPulseManager = manager.getPulseManager();
        mConnectionManager.registerReceiver(this);
    }

    /**
     * 解除连接当前的连接管理器
     */
    @CallSuper
    public void detach() {
        mDetach = true;
        if (mConnectionManager != null) {
            mConnectionManager.unRegisterReceiver(this);
        }
    }

    /**
     * 添加需要忽略的异常,当断开异常为该异常时,将不会进行重连.
     *
     * @param e 需要忽略的异常
     */
    public void addIgnoreException(Class<? extends Exception> e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.add(e);
        }
    }

    /**
     * 添加需要忽略的异常,当断开异常为该异常时,将不会进行重连.
     *
     * @param e 需要删除的异常
     */
    public void removeIgnoreException(Exception e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.remove(e.getClass());
        }
    }

    /**
     * 删除需要忽略的异常
     *
     * @param e 需要忽略的异常
     */
    public void removeIgnoreException(Class<? extends Exception> e) {
        synchronized (mIgnoreDisconnectExceptionList) {
            mIgnoreDisconnectExceptionList.remove(e);
        }
    }

    /**
     * 删除所有的忽略异常
     */
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
