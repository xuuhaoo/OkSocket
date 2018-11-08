package com.xuhao.didi.socket.client.sdk.client.action;

import com.xuhao.didi.core.iocore.interfaces.IOAction;

public interface IAction extends IOAction {
    //数据key
    String ACTION_DATA = "action_data";
    //socket读线程启动响应
    String ACTION_READ_THREAD_START = "action_read_thread_start";
    //socket读线程关闭响应
    String ACTION_READ_THREAD_SHUTDOWN = "action_read_thread_shutdown";
    //socket写线程启动响应
    String ACTION_WRITE_THREAD_START = "action_write_thread_start";
    //socket写线程关闭响应
    String ACTION_WRITE_THREAD_SHUTDOWN = "action_write_thread_shutdown";
    //socket连接服务器成功响应
    String ACTION_CONNECTION_SUCCESS = "action_connection_success";
    //socket连接服务器失败响应
    String ACTION_CONNECTION_FAILED = "action_connection_failed";
    //socket与服务器断开连接
    String ACTION_DISCONNECTION = "action_disconnection";

}