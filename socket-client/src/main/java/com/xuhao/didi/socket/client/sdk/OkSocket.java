package com.xuhao.didi.socket.client.sdk;


import com.xuhao.didi.socket.client.impl.client.ManagerHolder;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;

/**
 * OkSocket是一款轻量级的Socket通讯框架,可以提供单工,双工的TCP通讯.
 * 本类提供OkSocket的所有对外接口,使用OkSocket框架应从本类的open开启一个连接通道.
 * Created by xuhao on 2017/5/16.
 */
public class OkSocket {

    private static ManagerHolder holder = ManagerHolder.getInstance();

    /**
     * 获得一个SocketServer服务器.
     *
     * @param serverPort
     * @return
     */
    public static IRegister<IServerActionListener, IServerManager> server(int serverPort) {
        return (IRegister<IServerActionListener, IServerManager>) holder.getServer(serverPort);
    }

    /**
     * 开启一个socket通讯通道,参配为默认参配
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(ConnectionInfo connectInfo) {
        return holder.getConnection(connectInfo);
    }

    /**
     * 开启一个socket通讯通道,参配为默认参配
     *
     * @param ip   需要连接的主机IPV4地址
     * @param port 需要连接的主机开放的Socket端口号
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(String ip, int port) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info);
    }

    /**
     * 开启一个socket通讯通道
     * Deprecated please use {@link OkSocket#open(ConnectionInfo)}@{@link IConnectionManager#option(OkSocketOptions)}
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @param okOptions   连接参配{@link OkSocketOptions}
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     * @deprecated
     */
    public static IConnectionManager open(ConnectionInfo connectInfo, OkSocketOptions okOptions) {
        return holder.getConnection(connectInfo, okOptions);
    }

    /**
     * 开启一个socket通讯通道
     * Deprecated please use {@link OkSocket#open(String, int)}@{@link IConnectionManager#option(OkSocketOptions)}
     *
     * @param ip        需要连接的主机IPV4地址
     * @param port      需要连接的主机开放的Socket端口号
     * @param okOptions 连接参配{@link OkSocketOptions}
     * @return 该参数的连接管理器 {@link IConnectionManager}
     * @deprecated
     */
    public static IConnectionManager open(String ip, int port, OkSocketOptions okOptions) {
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info, okOptions);
    }
}
