package com.xuhao.android.server.action;

import com.xuhao.android.common.interfacies.dispatcher.IStateSender;

import java.io.Serializable;

public class ClientActionDispatcher implements IStateSender {

    @Override
    public void sendBroadcast(String action, Serializable serializable) {

    }

    @Override
    public void sendBroadcast(String action) {

    }
}
