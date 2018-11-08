package com.xuhao.didi.core.iocore.interfaces;

public interface IOAction {
    //收到推送消息响应
    String ACTION_READ_COMPLETE = "action_read_complete";
    //写给服务器响应
    String ACTION_WRITE_COMPLETE = "action_write_complete";
    //发送心跳请求
    String ACTION_PULSE_REQUEST = "action_pulse_request";
}
