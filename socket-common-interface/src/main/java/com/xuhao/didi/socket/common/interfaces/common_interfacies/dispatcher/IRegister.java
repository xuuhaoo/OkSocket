package com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher;

public interface IRegister<T, E> {
    /**
     * 注册一个回调接收器
     *
     * @param socketActionListener 回调接收器
     */
    E registerReceiver(T socketActionListener);

    /**
     * 解除回调接收器
     *
     * @param socketActionListener 注册时的接收器,需要解除的接收器
     */
    E unRegisterReceiver(T socketActionListener);
}
