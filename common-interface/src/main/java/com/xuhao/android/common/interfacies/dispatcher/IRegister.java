package com.xuhao.android.common.interfacies.dispatcher;

import android.content.BroadcastReceiver;

public interface IRegister<T> {
    /**
     * 注册一个回调广播接收器
     *
     * @param broadcastReceiver 回调广播接收器
     */
    void registerReceiver(BroadcastReceiver broadcastReceiver, String... action);

    /**
     * 注册一个回调接收器
     *
     * @param socketActionListener 回调接收器
     */
    void registerReceiver(final T socketActionListener);

    /**
     * 解除回调广播接收器
     *
     * @param broadcastReceiver 注册时的广播接收器,需要解除的广播接收器
     */
    void unRegisterReceiver(BroadcastReceiver broadcastReceiver);

    /**
     * 解除回调接收器
     *
     * @param socketActionListener 注册时的接收器,需要解除的接收器
     */
    void unRegisterReceiver(T socketActionListener);
}
