package com.xuhao.android.libsocket.sdk;


import android.app.Application;
import android.support.annotation.NonNull;

import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.server.IServerActionListener;
import com.xuhao.android.common.interfacies.server.IServerManager;
import com.xuhao.android.common.utils.ActivityStack;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.libsocket.impl.client.EnvironmentalManager;
import com.xuhao.android.libsocket.impl.client.ManagerHolder;
import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.connection.IConnectionManager;

/**
 * OkSocket是一款轻量级的Socket通讯框架,可以提供单工,双工的TCP通讯.
 * 本类提供OkSocket的所有对外接口,使用OkSocket框架应从本类的open开启一个连接通道.
 * Created by xuhao on 2017/5/16.
 */
public class OkSocket {

    private static Application app;

    private static boolean isInit = false;

    private static ManagerHolder holder = ManagerHolder.getInstance();

    /**
     * OkSocket框架初始化方法,使用open之前,务必调用该初始化方法,该方法尽可调用一次
     *
     * @param application Application上下文
     */
    public static void initialize(@NonNull Application application) {
        initialize(application, false);
    }

    /**
     * OkSocket框架初始化方法,使用open之前,务必调用该初始化方法,该方法尽可调用一次
     *
     * @param application Application上下文
     */
    public static void initialize(@NonNull Application application, boolean isDebug) {
        assertIsNotInit();
        isInit = true;
        OkSocketOptions.setIsDebug(isDebug);
        SLog.setIsDebug(isDebug);
        ActivityStack.init(application, isDebug);
        OkSocket.app = (Application) application.getApplicationContext();
        EnvironmentalManager.getIns().init(holder);
    }

    /**
     * 获得一个SocketServer服务器.
     *
     * @param serverPort
     * @return
     */
    public static IRegister<IServerActionListener, IServerManager> server(int serverPort) {
        assertIsInit();
        return (IRegister<IServerActionListener, IServerManager>) holder.getServer(serverPort, app);
    }

    /**
     * 开启一个socket通讯通道,参配为默认参配
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(ConnectionInfo connectInfo) {
        assertIsInit();
        return holder.getConnection(connectInfo, app);
    }

    /**
     * 开启一个socket通讯通道,参配为默认参配
     *
     * @param ip   需要连接的主机IPV4地址
     * @param port 需要连接的主机开放的Socket端口号
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(String ip, int port) {
        assertIsInit();
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info, app);
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
        assertIsInit();
        return holder.getConnection(connectInfo, app, okOptions);
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
        assertIsInit();
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.getConnection(info, app, okOptions);
    }

    /**
     * 设置OkSocket后台存活时间,为了保证耗电量,保证后台断开连接
     * 如果为-1,表示App至于后台时不进行连接断开操作.
     *
     * @param mills App至于后台后的存活的毫秒数.
     *              取值范围: -1为永久存活,取值范围[1000,Long.MAX]
     *              小于1000毫秒按照1000毫秒计算,具体最小值定义请见{@link EnvironmentalManager#DELAY_CONNECT_MILLS}
     */
    public static void setBackgroundSurvivalTime(long mills) {
        assertIsInit();
        EnvironmentalManager.getIns().setBackgroundLiveMills(mills);
    }

    /**
     * 获取当前框架允许的后台存活时间
     *
     * @return 后台存活时间
     */
    public static long getBackgroundSurvivalTime() {
        assertIsInit();
        return EnvironmentalManager.getIns().getBackgroundLiveMills();
    }

    private static void assertIsNotInit() throws RuntimeException {
        if (app != null || isInit) {
            throw new RuntimeException("不能初始化多次");
        }
    }

    private static void assertIsInit() throws RuntimeException {
        if (app == null) {
            throw new RuntimeException("上下文不能为空");
        }
        if (!isInit) {
            throw new RuntimeException("Socket需要先初始化,请先先初始化");
        }
    }


}
