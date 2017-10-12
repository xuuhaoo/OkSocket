package com.xuhao.android.libsocket.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;

import com.xuhao.android.libsocket.impl.abilities.IIOManager;
import com.xuhao.android.libsocket.impl.exceptions.UnconnectException;
import com.xuhao.android.libsocket.impl.nonblockio.IOManager;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.connection.AbsReconnectionManager;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.utils.SL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by xuhao on 2017/5/16.
 */

public class UnBlockConnectionManager extends AbsConnectionManager {
    /**
     * 唯一的Selector,管理多个channel
     */
    private static Selector selector;
    /**
     * Socket参配
     */
    private OkSocketOptions mOptions;
    /**
     * IO通讯管理器
     */
    private IIOManager mManager;
    /**
     * 本通道的key
     */
    private SelectionKey mSelectionKey;
    /**
     * 连接线程
     */
    private Thread mConnectThread = null;
    /**
     * Socket行为监听器
     */
    private SocketActionHandler mActionHandler = null;
    /**
     * 脉搏管理器
     */
    private PulseManager mPulseManager;
    /**
     * 能否连接
     */
    private boolean canConnect = true;
    /**
     * 连接超时处理Task
     */
    private Handler mConnectionTimeout = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                removeMessages(0);
                if (isConnect()) {
                    return;
                }
                SL.e("连接超时,终止连接");
                Exception exception = new UnconnectException("连接超时,终止连接");
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, exception);
            }
        }
    };

    protected UnBlockConnectionManager(Context context, ConnectionInfo info, OkSocketOptions okOptions) {
        super(context, info);
        SL.i("unblock connection init");
        mOptions = okOptions;
    }

    @Override
    @WorkerThread
    public synchronized void connect() {
        if (!canConnect) {
            return;
        }
        if (isConnect()) {
            return;
        }
        if (mConnectionInfo == null) {
            throw new UnconnectException("连接参数为空,检查连接参数");
        }
        if (mActionHandler != null) {
            mActionHandler.detach(this);
        }
        mActionHandler = new SocketActionHandler();
        mActionHandler.attach(this, this);

        if (mConnectionTimeout != null) {
            mConnectionTimeout.removeMessages(0);
        }
        mConnectionTimeout
                .sendMessageDelayed(mConnectionTimeout.obtainMessage(0), mOptions.getConnectTimeoutSecond() * 1000);
        mConnectThread = new ConnectionThread(
                mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " connect thread");
        mConnectThread.setDaemon(true);
        mConnectThread.start();
    }

    private class ConnectionThread extends Thread {
        public ConnectionThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                if (selector == null) {
                    selector = Selector.open();
                }
                SocketChannel channel = createChannel(mConnectionInfo);
                mSelectionKey = channel.register(selector, channel.validOps());
                if (listenConnect()) {
                    mConnectionTimeout.removeMessages(0);
                    sendBroadcast(IAction.ACTION_CONNECTION_SUCCESS);
                    resolveManager();
                } else {
                    throw new UnconnectException("listenConnect return false");
                }
            } catch (Exception e) {
                mConnectionTimeout.removeMessages(0);
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, new UnconnectException(e));
            }
        }
    }

    private SocketChannel createChannel(ConnectionInfo connectionInfo) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(connectionInfo.getIp(), connectionInfo.getPort());
        channel.connect(address);
        return channel;
    }

    private boolean listenConnect() throws IOException {
        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                return false;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isConnectable()) {
                    it.remove();
                    if (key.isValid()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 是否连接完毕？
                        boolean success = channel.finishConnect();
                        if (!success && !netIsAvailable()) {
                            // 异常
                            return false;
                        }
                        return success;
                    }
                }
            }
        }
    }

    private void resolveManager() throws IOException {
        mPulseManager = new PulseManager(this, mOptions);

        mManager = new IOManager(mContext, mSelectionKey, mOptions, UnBlockConnectionManager.this);
        mManager.resolve();
    }

    private boolean netIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return !(networkinfo == null || !networkinfo.isAvailable());
    }

    @Override
    public synchronized void disConnect(Exception exception) {
        if (mConnectThread != null) {
            mConnectThread.interrupt();
            mConnectThread = null;
        }
        if (mConnectionTimeout != null) {
            mConnectionTimeout.removeMessages(0);
        }
        if (mPulseManager != null) {
            mPulseManager.dead();
            mPulseManager = null;
        }
        if (mSelectionKey != null) {
            SocketChannel channel = (SocketChannel) mSelectionKey.channel();
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSelectionKey.cancel();
            mSelectionKey = null;
        }
        if (mManager != null) {
            mManager.close();
        }

        if (!canConnect) {
            sendBroadcast(IAction.ACTION_DISCONNECTION, exception);
        }

        if (mActionHandler != null) {
            mActionHandler.detach(this);
            mActionHandler = null;
        }
        canConnect = true;
    }

    @Override
    public void disConnect() {
        disConnect(null);
    }

    @Override
    public IConnectionManager send(ISendable sendable) {
        if (mManager != null && sendable != null && isConnect()) {
            mManager.send(sendable);
        }
        return this;
    }

    @Override
    public IConnectionManager option(OkSocketOptions okOptions) {
        if (mOptions == null) {
            return this;
        }
        mOptions = okOptions;
        if (mManager != null) {
            mManager.setOkOptions(mOptions);
        }
        if (mPulseManager != null) {
            mPulseManager.setOkOptions(mOptions);
        }
        EnvironmentalManager.getIns().setOkOptions(mOptions);
        return this;
    }

    @Override
    public OkSocketOptions getOption() {
        return mOptions;
    }

    @Override
    public boolean isConnect() {
        if (mSelectionKey == null) {
            return false;
        }
        SocketChannel channel = (SocketChannel) mSelectionKey.channel();
        return channel.isConnected() && !channel.isConnectionPending() && netIsAvailable();
    }

    @Override
    public PulseManager getPulseManager() {
        return mPulseManager;
    }

    @Override
    public void setIsConnectionHolder(boolean isHold) {
        mOptions = new OkSocketOptions.Builder(mOptions).setConnectionHolde(isHold).build();
    }

    @Override
    public AbsReconnectionManager getReconnectionManager() {
        return mOptions.getReconnectionManager();
    }
}
