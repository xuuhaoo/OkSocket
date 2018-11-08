package com.xuhao.didi.socket.client.sdk.client.bean;

/**
 * Created by xuhao on 2017/5/18.
 */

public interface IPulse {
    /**
     * 开始心跳
     */
    void pulse();

    /**
     * 触发一次心跳
     */
    void trigger();

    /**
     * 停止心跳
     */
    void dead();

    /**
     * 心跳返回后喂狗,ACK
     */
    void feed();
}

