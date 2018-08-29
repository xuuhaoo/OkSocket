package com.xuhao.android.server.action;

import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.server.impl.clientpojo.Client;

import java.io.Serializable;

import static com.xuhao.android.server.action.IAction.Client.ACTION_READ_COMPLETE;
import static com.xuhao.android.server.action.IAction.Client.ACTION_READ_THREAD_SHUTDOWN;
import static com.xuhao.android.server.action.IAction.Client.ACTION_READ_THREAD_START;
import static com.xuhao.android.server.action.IAction.Client.ACTION_WRITE_COMPLETE;
import static com.xuhao.android.server.action.IAction.Client.ACTION_WRITE_THREAD_SHUTDOWN;
import static com.xuhao.android.server.action.IAction.Client.ACTION_WRITE_THREAD_START;

public class ClientActionDispatcher implements IStateSender {

    private ClientActionListener mActionListener;

    private Client mClient;

    public ClientActionDispatcher(Client client, ClientActionListener actionListener) {
        mActionListener = actionListener;
        mClient = client;
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        if (mActionListener == null) {
            return;
        }
        dispatch(action, serializable);
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    private void dispatch(String action, Serializable serializable) {
        switch (action) {
            case ACTION_READ_THREAD_START: {
                try {
                    mActionListener.onClientReadReady(mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) serializable;
                    mActionListener.onClientReadDead(exception, mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_START: {
                try {
                    mActionListener.onClientWriteReady(mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) serializable;
                    mActionListener.onClientWriteDead(exception, mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) serializable;
                    mActionListener.onClientRead(data, mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_COMPLETE: {
                try {
                    ISendable data = (ISendable) serializable;
                    mActionListener.onClientWrite(data, mClient.getUniqueTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public interface ClientActionListener {
        void onClientReadReady(String tag);

        void onClientWriteReady(String tag);

        void onClientReadDead(Exception e, String tag);

        void onClientWriteDead(Exception e, String tag);

        void onClientRead(OriginalData originalData, String tag);

        void onClientWrite(ISendable sendable, String tag);
    }
}
