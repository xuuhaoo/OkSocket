package com.xuhao.android.libsocket.sdk.connection.interfacies;

import android.content.Context;

import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;

public interface ISocketActionListener {
    /**
     * Socket通讯IO线程的启动<br>
     * 该方法调用后IO线程将会正常工作
     *
     * @param context
     * @param action
     */
    void onSocketIOThreadStart(Context context, String action);

    /**
     * Socket通讯IO线程的关闭<br>
     * 该方法调用后IO线程将彻底死亡
     *
     * @param context
     * @param action
     * @param e 关闭时将会产生的异常,IO线程一般情况下都会有异常产生
     */
    void onSocketIOThreadShutdown(Context context, String action, Exception e);

    /**
     * Socket通讯读取到消息后的响应
     *
     * @param context
     * @param action
     * @param data 原始的读取到的数据{@link OriginalData}
     */
    void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data);

    /**
     * Socket通讯写出后的响应回调
     *
     * @param context
     * @param action
     * @param data 写出的数据{@link ISendable}
     */
    void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data);

    /**
     * Socket心跳发送后的回调<br>
     * 心跳发送是一个很特殊的写操作<br>
     * 该心跳发送后将不会回调{@link #onSocketWriteResponse(Context, ConnectionInfo, String, ISendable)}方法
     *
     * @param context
     * @param info 这次连接的连接信息
     * @param data 心跳发送数据{@link IPulseSendable}
     */
    void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data);

    /**
     * Socket断开后进行的回调<br>
     * 当Socket彻底断开后,系统会回调该方法
     *
     * @param context
     * @param info 这次连接的连接信息
     * @param action
     * @param e Socket断开时的异常信息,如果是正常断开(调用disconnect()),异常信息将为null.使用e变量时应该进行判空操作
     */
    void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e);

    /**
     * 当Socket连接建立成功后<br>
     * 系统会回调该方法,此时有可能读写线程还未启动完成,不过不会影响大碍<br>
     * 当回调此方法后,我们可以认为Socket连接已经建立完成,并且读写线程也初始化完
     *
     * @param context
     * @param info 这次连接的连接信息
     * @param action
     */
    void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action);

    /**
     * 当Socket连接失败时会进行回调<br>
     * 建立Socket连接,如果服务器出现故障,网络出现异常都将导致该方法被回调<br>
     * 系统回调此方法时,IO线程均未启动.如果IO线程启动将会回调{@link #onSocketDisconnection(Context, ConnectionInfo, String, Exception)}
     *
     * @param context
     * @param info 这次连接的连接信息
     * @param action
     * @param e 连接未成功建立的错误原因
     */
    void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e);
}
