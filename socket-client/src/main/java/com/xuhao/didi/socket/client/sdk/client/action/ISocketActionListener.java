package com.xuhao.didi.socket.client.sdk.client.action;


import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;

public interface ISocketActionListener {
    /**
     * Socket通讯IO线程的启动<br>
     * 该方法调用后IO线程将会正常工作<br>
     * 例如InputStream线程启动后,讲回调此方法,如果OutPutStream线程启动,也会回调此方法.<br>
     * 一次成功的双工通讯建立,会调用此方法两次.<br>
     *
     * @param action {@link IAction#ACTION_READ_THREAD_START}
     *               {@link  IAction#ACTION_WRITE_THREAD_START}
     */
    void onSocketIOThreadStart(String action);

    /**
     * Socket通讯IO线程的关闭<br>
     * 该方法调用后IO线程将彻底死亡<br>
     * 例如InputStream线程销毁后,讲回调此方法,如果OutPutStream线程销毁,也会回调此方法.<br>
     * 一次成功的双工通讯销毁,会调用此方法两次.<br>
     *
     * @param action {@link IAction#ACTION_READ_THREAD_SHUTDOWN}
     *               {@link  IAction#ACTION_WRITE_THREAD_SHUTDOWN}
     * @param e      线程关闭所遇到的异常信息,正常断开也可能会有异常信息.
     */
    void onSocketIOThreadShutdown(String action, Exception e);

    /**
     * Socket通讯从服务器读取到消息后的响应<br>
     *
     * @param action {@link IAction#ACTION_READ_COMPLETE}
     * @param data   原始的读取到的数据{@link OriginalData}
     */
    void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data);

    /**
     * Socket通讯写出后的响应回调<br>
     *
     * @param action {@link IAction#ACTION_WRITE_COMPLETE}
     * @param data   写出的数据{@link ISendable}
     */
    void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data);

    /**
     * Socket心跳发送后的回调<br>
     * 心跳发送是一个很特殊的写操作<br>
     * 该心跳发送后将不会回调{@link #onSocketWriteResponse(ConnectionInfo, String, ISendable)}方法<br>
     *
     * @param info 这次连接的连接信息
     * @param data 心跳发送数据{@link IPulseSendable}
     */
    void onPulseSend(ConnectionInfo info, IPulseSendable data);

    /**
     * Socket断开后进行的回调<br>
     * 当Socket彻底断开后,系统会回调该方法<br>
     *
     * @param info   这次连接的连接信息
     * @param action {@link IAction#ACTION_DISCONNECTION}
     * @param e      Socket断开时的异常信息,如果是正常断开(调用disconnect()),异常信息将为null.使用e变量时应该进行判空操作
     */
    void onSocketDisconnection(ConnectionInfo info, String action, Exception e);

    /**
     * 当Socket连接建立成功后<br>
     * 系统会回调该方法,此时有可能读写线程还未启动完成,不过不会影响大碍<br>
     * 当回调此方法后,我们可以认为Socket连接已经建立完成,并且读写线程也初始化完<br>
     *
     * @param info   这次连接的连接信息
     * @param action {@link IAction#ACTION_CONNECTION_SUCCESS}
     */
    void onSocketConnectionSuccess(ConnectionInfo info, String action);

    /**
     * 当Socket连接失败时会进行回调<br>
     * 建立Socket连接,如果服务器出现故障,网络出现异常都将导致该方法被回调<br>
     * 系统回调此方法时,IO线程均未启动.如果IO线程启动将会回调{@link #onSocketDisconnection(ConnectionInfo, String, Exception)}<br>
     *
     * @param info   这次连接的连接信息
     * @param action {@link IAction#ACTION_CONNECTION_FAILED}
     * @param e      连接未成功建立的错误原因
     */
    void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e);
}
