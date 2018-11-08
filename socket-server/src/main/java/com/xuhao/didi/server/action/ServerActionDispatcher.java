package com.xuhao.didi.server.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xuhao.didi.common.interfacies.dispatcher.IRegister;
import com.xuhao.didi.common.interfacies.dispatcher.IStateSender;
import com.xuhao.didi.common.interfacies.server.IClient;
import com.xuhao.didi.common.interfacies.server.IClientPool;
import com.xuhao.didi.common.interfacies.server.IServerActionListener;
import com.xuhao.didi.common.interfacies.server.IServerManager;
import com.xuhao.didi.common.utils.SocketBroadcastManager;
import com.xuhao.didi.server.impl.OkServerOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import static com.xuhao.didi.server.action.IAction.Server.ACTION_CLIENT_CONNECTED;
import static com.xuhao.didi.server.action.IAction.Server.ACTION_CLIENT_DISCONNECTED;
import static com.xuhao.didi.server.action.IAction.Server.ACTION_SERVER_ALLREADY_SHUTDOWN;
import static com.xuhao.didi.server.action.IAction.Server.ACTION_SERVER_LISTENING;
import static com.xuhao.didi.server.action.IAction.Server.ACTION_SERVER_WILL_BE_SHUTDOWN;
import static com.xuhao.didi.server.action.IAction.Server.SERVER_ACTION_DATA;


/**
 * 服务器状态机
 * Created by didi on 2018/4/19.
 */
public class ServerActionDispatcher implements IRegister<IServerActionListener, IServerManager>, IStateSender {
    /**
     * 回调列表
     */
    private Vector<IServerActionListener> mResponseHandlerList = new Vector<>();
    /**
     * 服务器端口
     */
    private int mServerPort;
    /**
     * 客户端池子
     */
    private IClientPool<IClient, String> mClientPool;
    /**
     * 服务器管理器实例
     */
    private IServerManager<OkServerOptions> mServerManager;

    public ServerActionDispatcher(Context context, IServerManager<OkServerOptions> manager) {
        this.mServerManager = manager;
    }

    public void setServerPort(int localPort) {
        mServerPort = localPort;
    }

    public void setClientPool(IClientPool<IClient, String> clientPool) {
        mClientPool = clientPool;
    }

    @Override
    public IServerManager<OkServerOptions> registerReceiver(final IServerActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            if (!mResponseHandlerList.contains(socketResponseHandler)) {
                mResponseHandlerList.add(socketResponseHandler);
            }
        }
        return mServerManager;
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(IServerActionListener socketResponseHandler) {
        mResponseHandlerList.remove(socketResponseHandler);
        return mServerManager;
    }

    /**
     * 分发收到的响应
     *
     * @param context
     * @param intent
     * @param responseHandler
     */
    private void dispatchActionToListener(Context context, Intent intent, IServerActionListener responseHandler) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_SERVER_LISTENING: {
                try {
                    responseHandler.onServerListening(context, mServerPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_CONNECTED: {
                try {
                    IClient client = (IClient) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onClientConnected(context, client, mServerPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_DISCONNECTED: {
                try {
                    IClient client = (IClient) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onClientDisconnected(context, client, mServerPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_WILL_BE_SHUTDOWN: {
                try {
                    Throwable throwable = (Throwable) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onServerWillBeShutdown(context, mServerPort, mServerManager, mClientPool, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_ALLREADY_SHUTDOWN: {
                try {
                    responseHandler.onServerAlreadyShutdown(context, mServerPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {

    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }
}
