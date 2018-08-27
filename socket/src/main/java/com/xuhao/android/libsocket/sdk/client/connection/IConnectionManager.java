package com.xuhao.android.libsocket.sdk.client.connection;

import com.xuhao.android.libsocket.impl.client.PulseManager;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.connection.abilities.IConfiguration;
import com.xuhao.android.libsocket.sdk.client.connection.abilities.IConnectable;
import com.xuhao.android.libsocket.sdk.client.connection.abilities.IRegister;
import com.xuhao.android.libsocket.sdk.client.connection.abilities.ISender;
import com.xuhao.android.libsocket.sdk.client.connection.abilities.IDisConnectable;

/**
 * Created by xuhao on 2017/5/16.
 */

public interface IConnectionManager extends
        IConfiguration,
        IConnectable,
        IDisConnectable,
        ISender<IConnectionManager>,
        IRegister {
    /**
     * 是否连接
     *
     * @return true 已连接,false 未连接
     */
    boolean isConnect();

    /**
     * 是否处在断开连接的阶段.
     *
     * @return true 正在断开连接,false连接中或者已断开.
     */
    boolean isDisconnecting();

    /**
     * 获取到心跳管理器,用来配置心跳参数和心跳行为.
     *
     * @return 心跳管理器
     */
    PulseManager getPulseManager();

    /**
     * 是否OkSocket保存此次连接
     *
     * @param isHold true 进行保留缓存管理.false 不进行保存缓存管理.
     */
    void setIsConnectionHolder(boolean isHold);

    /**
     * 获得连接信息
     *
     * @return 连接信息
     */
    ConnectionInfo getConnectionInfo();

    /**
     * 将当前的连接管理器中的连接信息进行切换.
     *
     * @param info 新的连接信息
     */
    void switchConnectionInfo(ConnectionInfo info);

    /**
     * 获得重连管理器,用来配置重连管理器
     *
     * @return 重连管理器
     */
    AbsReconnectionManager getReconnectionManager();

}

