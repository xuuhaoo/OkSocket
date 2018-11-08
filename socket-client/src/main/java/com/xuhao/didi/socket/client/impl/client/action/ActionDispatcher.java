package com.xuhao.didi.socket.client.impl.client.action;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.iocore.interfaces.IStateSender;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.common.interfaces.basic.AbsLoopThread;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import static com.xuhao.didi.core.iocore.interfaces.IOAction.ACTION_PULSE_REQUEST;
import static com.xuhao.didi.core.iocore.interfaces.IOAction.ACTION_READ_COMPLETE;
import static com.xuhao.didi.core.iocore.interfaces.IOAction.ACTION_WRITE_COMPLETE;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_CONNECTION_FAILED;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_CONNECTION_SUCCESS;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_DISCONNECTION;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_READ_THREAD_SHUTDOWN;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_READ_THREAD_START;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_SHUTDOWN;
import static com.xuhao.didi.socket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_START;


/**
 * 状态机
 * Created by didi on 2018/4/19.
 */
public class ActionDispatcher implements IRegister<ISocketActionListener, IConnectionManager>, IStateSender {
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
     * 除了广播还支持回调
     */
    private Vector<ISocketActionListener> mResponseHandlerSet = new Vector<>();
    /**
     * 连接信息
     */
    private ConnectionInfo mConnectionInfo;
    /**
     * 连接管理器
     */
    private IConnectionManager mManager;


    public ActionDispatcher(ConnectionInfo info, IConnectionManager manager) {
        mManager = manager;
        mConnectionInfo = info;
    }

    @Override
    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            synchronized (mResponseHandlerSet) {
                if (!mResponseHandlerSet.contains(socketResponseHandler)) {
                    mResponseHandlerSet.add(socketResponseHandler);
                }
            }
        }
        return mManager;
    }

    @Override
    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        mResponseHandlerSet.remove(socketResponseHandler);
        return mManager;
    }

    /**
     * 分发收到的响应
     *
     * @param action
     * @param arg
     * @param responseHandler
     */
    private void dispatchActionToListener(String action, Serializable arg, ISocketActionListener responseHandler) {
        switch (action) {
            case ACTION_CONNECTION_SUCCESS: {
                try {
                    responseHandler.onSocketConnectionSuccess(mConnectionInfo, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CONNECTION_FAILED: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketConnectionFailed(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_DISCONNECTION: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketDisconnection(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) arg;
                    responseHandler.onSocketReadResponse(mConnectionInfo, action, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_THREAD_START:
            case ACTION_WRITE_THREAD_START: {
                try {
                    responseHandler.onSocketIOThreadStart(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_COMPLETE: {
                try {
                    ISendable sendable = (ISendable) arg;
                    responseHandler.onSocketWriteResponse(mConnectionInfo, action, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_SHUTDOWN:
            case ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketIOThreadShutdown(action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_PULSE_REQUEST: {
                try {
                    IPulseSendable sendable = (IPulseSendable) arg;
                    responseHandler.onPulseSend(mConnectionInfo, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        OkSocketOptions option = mManager.getOption();
        if (option != null && option.isCallbackInThread()) {
            ActionBean bean = new ActionBean(action, serializable, this);
            ACTION_QUEUE.offer(bean);
        } else {
            synchronized (mResponseHandlerSet) {
                Iterator<ISocketActionListener> it = mResponseHandlerSet.iterator();
                while (it.hasNext()) {
                    ISocketActionListener listener = it.next();
                    this.dispatchActionToListener(action, serializable, listener);
                }
            }
        }
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        mConnectionInfo = connectionInfo;
    }

    /**
     * 分发线程
     */
    private static class DispatchThread extends AbsLoopThread {
        public DispatchThread() {
            super("dispatch_thread");
        }

        @Override
        protected void runInLoopThread() throws Exception {
            ActionBean actionBean = ACTION_QUEUE.take();
            if (actionBean != null && actionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = actionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerSet) {
                    Iterator<ISocketActionListener> it = actionDispatcher.mResponseHandlerSet.iterator();
                    while (it.hasNext()) {
                        ISocketActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(actionBean.mAction, actionBean.arg, listener);
                    }
                }
            }
        }

        @Override
        protected void loopFinish(Exception e) {

        }
    }

    /**
     * 行为Bean
     */
    private static class ActionBean {
        public ActionBean(String action, Serializable arg, ActionDispatcher dispatcher) {
            mAction = action;
            this.arg = arg;
            mDispatcher = dispatcher;
        }

        String mAction = "";
        Serializable arg;
        ActionDispatcher mDispatcher;
    }

}
