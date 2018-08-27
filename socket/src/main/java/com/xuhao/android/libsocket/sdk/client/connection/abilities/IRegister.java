package com.xuhao.android.libsocket.sdk.client.connection.abilities;

import android.content.BroadcastReceiver;

import com.xuhao.android.libsocket.sdk.client.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.client.connection.interfacies.IAction;
import com.xuhao.android.libsocket.sdk.client.connection.interfacies.ISocketActionListener;

/**
 * Created by xuhao on 2017/5/17.
 */

public interface IRegister {
    /**
     * 注册一个回调广播接收器
     *
     * @param broadcastReceiver 回调广播接收器
     * @param action            {@link IAction}
     */
    void registerReceiver(BroadcastReceiver broadcastReceiver, String... action);

    /**
     * 注册一个回调接收器
     *
     * @param socketResponseHandler 回调接收器 {@link SocketActionAdapter}
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
