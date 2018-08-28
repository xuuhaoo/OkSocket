package com.xuhao.android.server.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xuhao.android.common.interfacies.client.IClient;
import com.xuhao.android.common.interfacies.client.IClientPool;
import com.xuhao.android.common.interfacies.dispatcher.IRegister;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IServerActionListener;
import com.xuhao.android.common.utils.SocketBroadcastManager;

import java.io.Serializable;
import java.util.HashMap;

import static com.xuhao.android.server.action.IAction.ACTION_CLIENT_CONNECTED;
import static com.xuhao.android.server.action.IAction.ACTION_CLIENT_DISCONNECTED;
import static com.xuhao.android.server.action.IAction.ACTION_SERVER_ALLREADY_SHUTDOWN;
import static com.xuhao.android.server.action.IAction.ACTION_SERVER_LISTEN_FAILED;
import static com.xuhao.android.server.action.IAction.ACTION_SERVER_LISTEN_SUCCESS;
import static com.xuhao.android.server.action.IAction.ACTION_SERVER_WILL_BE_SHUTDOWN;
import static com.xuhao.android.server.action.IAction.SERVER_ACTION_DATA;


/**
 * 服务器状态机
 * Created by didi on 2018/4/19.
 */
public class ServerActionDispatcher implements IRegister<IServerActionListener>, IStateSender {
    /**
     * 每个连接一个广播管理器不会串
     */
    private SocketBroadcastManager mSocketBroadcastManager;
    /**
     * 除了广播还支持回调
     */
    private HashMap<IServerActionListener, BroadcastReceiver> mResponseHandlerMap = new HashMap<>();
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 服务器端口
     */
    private int mLocalPort;
    /**
     * 客户端池子
     */
    private IClientPool<IClient, String> mClientPool;

    public ServerActionDispatcher(Context context, IClientPool<IClient, String> clientPool) {
        mContext = context.getApplicationContext();
        mSocketBroadcastManager = new SocketBroadcastManager(mContext);
    }

    public void setLocalPort(int localPort) {
        mLocalPort = localPort;
    }

    public void setClientPool(IClientPool<IClient, String> clientPool) {
        mClientPool = clientPool;
    }

    @Override
    public void registerReceiver(BroadcastReceiver broadcastReceiver, String... action) {
        IntentFilter intentFilter = new IntentFilter();
        if (action != null) {
            for (int i = 0; i < action.length; i++) {
                intentFilter.addAction(action[i]);
            }
        }
        mSocketBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void registerReceiver(final IServerActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            if (!mResponseHandlerMap.containsKey(socketResponseHandler)) {
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        dispatchActionToListener(context, intent, socketResponseHandler);
                    }
                };
                registerReceiver(broadcastReceiver,
                        ACTION_SERVER_LISTEN_SUCCESS,
                        ACTION_SERVER_LISTEN_FAILED,
                        ACTION_CLIENT_CONNECTED,
                        ACTION_CLIENT_DISCONNECTED,
                        ACTION_SERVER_WILL_BE_SHUTDOWN,
                        ACTION_SERVER_ALLREADY_SHUTDOWN);
                synchronized (mResponseHandlerMap) {
                    mResponseHandlerMap.put(socketResponseHandler, broadcastReceiver);
                }
            }
        }
    }

    @Override
    public void unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        mSocketBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void unRegisterReceiver(IServerActionListener socketResponseHandler) {
        synchronized (mResponseHandlerMap) {
            BroadcastReceiver broadcastReceiver = mResponseHandlerMap.get(socketResponseHandler);
            mResponseHandlerMap.remove(socketResponseHandler);
            unRegisterReceiver(broadcastReceiver);
        }
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
            case ACTION_SERVER_LISTEN_SUCCESS: {
                try {
                    responseHandler.onServerListenSuccess(context, mLocalPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_LISTEN_FAILED: {
                try {
                    Throwable throwable = (Throwable) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onServerListenFailed(context, mLocalPort, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_CONNECTED: {
                try {
                    IClient client = (IClient) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onClientConnected(context, client, mLocalPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_DISCONNECTED: {
                try {
                    IClient client = (IClient) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onClientDisconnected(context, client, mLocalPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_WILL_BE_SHUTDOWN: {
                try {
                    responseHandler.onServerWillBeShutdown(context, mLocalPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_ALLREADY_SHUTDOWN: {
                try {
                    Throwable throwable = (Throwable) intent.getSerializableExtra(SERVER_ACTION_DATA);
                    responseHandler.onServerAllreadyShutdown(context, mLocalPort, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        Intent intent = new Intent(action);
        intent.putExtra(SERVER_ACTION_DATA, serializable);
        mSocketBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }
}
