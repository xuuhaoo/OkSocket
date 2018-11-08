package com.xuhao.didi.libsocket.impl.client.action;

import android.content.Context;

import com.xuhao.didi.common.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.libsocket.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.didi.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.didi.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.didi.libsocket.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.libsocket.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.libsocket.sdk.client.connection.IConnectionManager;

/**
 * Created by xuhao on 2017/5/18.
 */

public class SocketActionHandler extends SocketActionAdapter {
    private IConnectionManager mManager;

    private OkSocketOptions.IOThreadMode mCurrentThreadMode;

    private boolean iOThreadIsCalledDisconnect = false;

    public SocketActionHandler() {

    }

    public void attach(IConnectionManager manager, IRegister<ISocketActionListener,IConnectionManager> register) {
        this.mManager = manager;
        register.registerReceiver(this);
    }

    public void detach(IRegister register) {
        register.unRegisterReceiver(this);
    }

    @Override
    public void onSocketIOThreadStart(Context context, String action) {
        if (mManager.getOption().getIOThreadMode() != mCurrentThreadMode) {
            mCurrentThreadMode = mManager.getOption().getIOThreadMode();
        }
        iOThreadIsCalledDisconnect = false;
    }

    @Override
    public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
        if (mCurrentThreadMode != mManager.getOption().getIOThreadMode()) {//切换线程模式,不需要断开连接
            //do nothing
        } else {//多工模式
            if (!iOThreadIsCalledDisconnect) {//保证只调用一次,多工多线程,会调用两次
                iOThreadIsCalledDisconnect = true;
                if (!(e instanceof ManuallyDisconnectException)) {
                    mManager.disconnect(e);
                }
            }
        }
    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
        mManager.disconnect(e);
    }
}
