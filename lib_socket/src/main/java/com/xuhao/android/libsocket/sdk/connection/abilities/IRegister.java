package com.xuhao.android.libsocket.sdk.connection.abilities;

import android.content.BroadcastReceiver;

import com.xuhao.android.libsocket.sdk.connection.interfacies.ISocketActionListener;

/**
 * Created by xuhao on 2017/5/17.
 */

public interface IRegister {
    void registerReceiver(BroadcastReceiver broadcastReceiver, String... action);

    void registerReceiver(final ISocketActionListener socketResponseHandler);

    void unRegisterReceiver(BroadcastReceiver broadcastReceiver);

    void unRegisterReceiver(ISocketActionListener socketResponseHandler);



}
