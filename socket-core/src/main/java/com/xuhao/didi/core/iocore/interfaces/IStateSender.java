package com.xuhao.didi.core.iocore.interfaces;

import java.io.Serializable;

/**
 * Created by xuhao on 2017/5/17.
 */

public interface IStateSender {

    void sendBroadcast(String action, Serializable serializable);

    void sendBroadcast(String action);
}
