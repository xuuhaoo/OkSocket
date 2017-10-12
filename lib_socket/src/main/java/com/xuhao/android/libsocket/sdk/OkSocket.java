package com.xuhao.android.libsocket.sdk;


import android.app.Application;
import android.support.annotation.NonNull;

import com.xuhao.android.libsocket.impl.EnvironmentalManager;
import com.xuhao.android.libsocket.impl.ManagerHolder;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;

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
        OkSocketOptions.isDebug = isDebug;
        OkSocket.app = (Application) application.getApplicationContext();
        //保证混淆时的Builder
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(OkSocketOptions.getDefault());
        EnvironmentalManager.getIns().init(app, holder, builder.build());
    }

    /**
     * 开启一个socket通讯通道
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @return 该参数的连接管理器 {@link IConnectionManager}
     */
    public static IConnectionManager open(ConnectionInfo connectInfo) {
        assertIsInit();
        return holder.get(connectInfo, app, OkSocketOptions.getDefault());
    }

    /**
     * 开启一个socket通讯通道
     *
     * @param connectInfo 连接信息{@link ConnectionInfo}
     * @param okOptions 连接参配{@link OkSocketOptions}
     * @return 该参数的连接管理器 {@link IConnectionManager} 连接参数仅作为配置该通道的参配,不影响全局参配
     */
    public static IConnectionManager open(ConnectionInfo connectInfo, OkSocketOptions okOptions) {
        assertIsInit();
        return holder.get(connectInfo, app, okOptions);
    }

    /**
     * 开启一个socket通讯通道
     *
     * @param ip 需要连接的主机IPV4地址
     * @param port 需要连接的主机开放的Socket端口号
     * @return 该参数的连接管理器 {@link IConnectionManager}
     */
    public static IConnectionManager open(String ip, int port) {
        assertIsInit();
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.get(info, app, OkSocketOptions.getDefault());
    }

    /**
     * 开启一个socket通讯通道
     *
     * @param ip 需要连接的主机IPV4地址
     * @param port 需要连接的主机开放的Socket端口号
     * @param okOptions 连接参配{@link OkSocketOptions}
     * @return 该参数的连接管理器 {@link IConnectionManager}
     */
    public static IConnectionManager open(String ip, int port, OkSocketOptions okOptions) {
        assertIsInit();
        ConnectionInfo info = new ConnectionInfo(ip, port);
        return holder.get(info, app, okOptions);
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
