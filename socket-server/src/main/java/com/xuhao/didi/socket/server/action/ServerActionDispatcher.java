package com.xuhao.didi.socket.server.action;

import com.xuhao.didi.core.iocore.interfaces.IStateSender;
import com.xuhao.didi.socket.common.interfaces.basic.AbsLoopThread;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.server.impl.OkServerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xuhao.didi.socket.server.action.IAction.Server.ACTION_CLIENT_CONNECTED;
import static com.xuhao.didi.socket.server.action.IAction.Server.ACTION_CLIENT_DISCONNECTED;
import static com.xuhao.didi.socket.server.action.IAction.Server.ACTION_SERVER_ALLREADY_SHUTDOWN;
import static com.xuhao.didi.socket.server.action.IAction.Server.ACTION_SERVER_LISTENING;
import static com.xuhao.didi.socket.server.action.IAction.Server.ACTION_SERVER_WILL_BE_SHUTDOWN;


/**
 * 服务器状态机
 * Created by didi on 2018/4/19.
 */
public class ServerActionDispatcher implements IRegister<IServerActionListener, IServerManager>, IStateSender {
    /**
     * 线程回调管理Handler
     */
    private static final DispatchThread HANDLE_THREAD = new DispatchThread();

    /**
     * 事件消费队列
     */
    private static final LinkedBlockingQueue<ActionBean> ACTION_QUEUE = new LinkedBlockingQueue();

    static {
        //启动分发线程
        HANDLE_THREAD.start();
    }

    /**
     * 回调列表
     */
    private volatile List<IServerActionListener> mResponseHandlerList = new ArrayList<>();
    /**
     * 服务器端口
     */
    private volatile int mServerPort;
    /**
     * 客户端池子
     */
    private volatile IClientPool<IClient, String> mClientPool;
    /**
     * 服务器管理器实例
     */
    private volatile IServerManager<OkServerOptions> mServerManager;

    public ServerActionDispatcher(IServerManager<OkServerOptions> manager) {
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
            synchronized (mResponseHandlerList) {
                if (!mResponseHandlerList.contains(socketResponseHandler)) {
                    mResponseHandlerList.add(socketResponseHandler);
                }
            }
        }
        return mServerManager;
    }

    @Override
    public IServerManager<OkServerOptions> unRegisterReceiver(IServerActionListener socketResponseHandler) {
        synchronized (mResponseHandlerList) {
            mResponseHandlerList.remove(socketResponseHandler);
        }
        return mServerManager;
    }

    /**
     * 分发收到的响应
     *
     * @param action
     * @param responseHandler
     */
    private void dispatchActionToListener(String action, Object arg, IServerActionListener responseHandler) {
        switch (action) {
            case ACTION_SERVER_LISTENING: {
                try {
                    responseHandler.onServerListening(mServerPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_CONNECTED: {
                try {
                    IClient client = (IClient) arg;
                    responseHandler.onClientConnected(client, mServerPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CLIENT_DISCONNECTED: {
                try {
                    IClient client = (IClient) arg;
                    responseHandler.onClientDisconnected(client, mServerPort, mClientPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_WILL_BE_SHUTDOWN: {
                try {
                    Throwable throwable = (Throwable) arg;
                    responseHandler.onServerWillBeShutdown(mServerPort, mServerManager, mClientPool, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_SERVER_ALLREADY_SHUTDOWN: {
                try {
                    responseHandler.onServerAlreadyShutdown(mServerPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        ActionBean bean = new ActionBean(action, serializable, this);
        ACTION_QUEUE.offer(bean);
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    /**
     * 行为封装
     */
    protected static class ActionBean {
        public ActionBean(String action, Serializable arg, ServerActionDispatcher dispatcher) {
            mAction = action;
            this.arg = arg;
            mDispatcher = dispatcher;
        }

        String mAction = "";
        Serializable arg;
        ServerActionDispatcher mDispatcher;
    }

    /**
     * 分发线程
     */
    private static class DispatchThread extends AbsLoopThread {
        public DispatchThread() {
            super("server_action_dispatch_thread");
        }

        @Override
        protected void runInLoopThread() throws Exception {
            ActionBean actionBean = ACTION_QUEUE.take();
            if (actionBean != null && actionBean.mDispatcher != null) {
                ServerActionDispatcher actionDispatcher = actionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerList) {
                    List<IServerActionListener> list = new ArrayList<>(actionDispatcher.mResponseHandlerList);
                    Iterator<IServerActionListener> it = list.iterator();
                    while (it.hasNext()) {
                        IServerActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(actionBean.mAction, actionBean.arg, listener);
                    }
                }
            }
        }

        @Override
        protected void loopFinish(Exception e) {

        }
    }

}
