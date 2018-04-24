package com.xuhao.android.libsocket.sdk.connection.abilities;

import android.content.BroadcastReceiver;

import com.xuhao.android.libsocket.sdk.connection.interfacies.ISocketActionListener;

/**
 * Created by xuhao on 2017/5/17.
 */

public interface IRegister {
    /**
     * 注册一个回调广播接收器
     *
     * @param broadcastReceiver 回调广播接收器
     * @param action            {@link com.xuhao.android.libsocket.sdk.connection.interfacies.IAction}
     */
    void registerReceiver(BroadcastReceiver broadcastReceiver, String... action);

    /**
     * 注册一个回调接收器
     *
     * @param socketResponseHandler 回调接收器 {@link com.xuhao.android.libsocket.sdk.SocketActionAdapter}
     */
    void registerReceiver(final ISocketActionListener socketResponseHandler);

    /**
     * 解除回调广播接收器
     *
     * @param broadcastReceiver 注册时的广播接收器,需要解除的广播接收器
     */
    void unRegisterReceiver(BroadcastReceiver broadcastReceiver);

    /**
     * 解除回调接收器
     *
     * @param socketResponseHandler 注册时的接收器,需要解除的接收器
     */
    void unRegisterReceiver(ISocketActionListener socketResponseHandler);
}
