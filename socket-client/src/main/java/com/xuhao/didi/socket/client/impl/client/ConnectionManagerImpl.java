package com.xuhao.didi.socket.client.impl.client;

import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.socket.client.impl.client.action.SocketActionHandler;
import com.xuhao.didi.socket.client.impl.client.iothreads.IOThreadManager;
import com.xuhao.didi.socket.client.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.didi.socket.client.impl.exceptions.UnconnectException;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.OkSocketSSLConfig;
import com.xuhao.didi.socket.client.sdk.client.action.IAction;
import com.xuhao.didi.socket.client.sdk.client.connection.AbsReconnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.IIOManager;
import com.xuhao.didi.socket.common.interfaces.default_protocol.DefaultX509ProtocolTrustManager;
import com.xuhao.didi.socket.common.interfaces.utils.TextUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by xuhao on 2017/5/16.
 */
public class ConnectionManagerImpl extends AbsConnectionManager {
    /**
     * 套接字
     */
    private Socket mSocket;
    /**
     * socket参配项
     */
    private OkSocketOptions mOptions;
    /**
     * IO通讯管理器
     */
    private IIOManager mManager;
    /**
     * 连接线程
     */
    private Thread mConnectThread;
    /**
     * Socket行为监听器
     */
    private SocketActionHandler mActionHandler;
    /**
     * 脉搏管理器
     */
    private PulseManager mPulseManager;
    /**
     * 重新连接管理器
     */
    private AbsReconnectionManager mReconnectionManager;
    /**
     * 能否连接
     */
    private volatile boolean canConnect = true;
    /**
     * 是否正在断开
     */
    private volatile boolean isDisconnecting = false;
    /**
     * 是否连接超时
     */
    private volatile boolean isConnectTimeout = false;

    /**
     * 连接超时处理Task
     */
//    private Handler mConnectionTimeout = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                isConnectTimeout = true;
//                removeCallbacksAndMessages(null);
//                if (mSocket != null && mSocket.isConnected()) {
//                    isConnectTimeout = false;
//                    return;
//                }
//                try {
//                    if (mSocket != null) {
//                        mSocket.close();
//                    }
//                } catch (IOException e) {
//                    //ignore
//                }
//
//                SLog.e(mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + "连接超时,终止连接");
//                Exception exception = new UnconnectException(mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort
//                        () + "连接超时,终止连接");
//                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, exception);
//            }
//        }
//    };
    protected ConnectionManagerImpl(ConnectionInfo info) {
        super(info);
        String ip = "";
        String port = "";
        if (info != null) {
            ip = info.getIp();
            port = info.getPort() + "";
        }
        SLog.i("block connection init with:" + ip + ":" + port);
    }

    @Override
    public synchronized void connect() {
        if (!canConnect) {
            return;
        }
        if (isConnect()) {
            return;
        }
        isDisconnecting = false;
        if (mConnectionInfo == null) {
            throw new UnconnectException("连接参数为空,检查连接参数");
        }
        if (mActionHandler != null) {
            mActionHandler.detach(this);
        }
        mActionHandler = new SocketActionHandler();
        mActionHandler.attach(this, this);

        if (mReconnectionManager != null) {
            mReconnectionManager.detach();
        }
        mReconnectionManager = mOptions.getReconnectionManager();
        if (mReconnectionManager != null) {
            mReconnectionManager.attach(this);
            SLog.i("ReconnectionManager is attached.");
        }
        try {
            mSocket = getSocketByConfig();
        } catch (Exception e) {
            throw new UnconnectException("创建Socket失败.", e);
        }

        String info = mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort();
        mConnectThread = new ConnectionThread(" Connect thread for " + info);
        mConnectThread.setDaemon(true);
        mConnectThread.start();
    }

    private Socket getSocketByConfig() throws Exception {
        //自定义socket操作
        if (mOptions.getOkSocketFactory() != null) {
            return mOptions.getOkSocketFactory().createSocket(mConnectionInfo, mOptions);
        }

        //默认操作
        OkSocketSSLConfig config = mOptions.getSSLConfig();
        if (config == null) {
            return new Socket();
        }

        SSLSocketFactory factory = config.getCustomSSLFactory();
        if (factory == null) {
            String protocol = "SSL";
            if (!TextUtils.isEmpty(config.getProtocol())) {
                protocol = config.getProtocol();
            }

            TrustManager[] trustManagers = config.getTrustManagers();
            if (trustManagers == null || trustManagers.length == 0) {
                //缺省信任所有证书
                trustManagers = new TrustManager[]{new DefaultX509ProtocolTrustManager()};
            }

            try {
                SSLContext sslContext = SSLContext.getInstance(protocol);
                sslContext.init(config.getKeyManagers(), trustManagers, new SecureRandom());
                return sslContext.getSocketFactory().createSocket();
            } catch (Exception e) {
                SLog.e(e.getMessage());
                return new Socket();
            }

        } else {
            try {
                return factory.createSocket();
            } catch (IOException e) {
                SLog.e(e.getMessage());
                return new Socket();
            }
        }
    }

    private class ConnectionThread extends Thread {
        public ConnectionThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                if (mSocket.isClosed() || mSocket.isConnected()) {
                    return;
                }
                if (!canConnect) {
                    return;
                }
                canConnect = false;
                isConnectTimeout = false;
                SLog.i("Start connect: " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " socket server...");
                mSocket.connect(new InetSocketAddress(mConnectionInfo.getIp(), mConnectionInfo.getPort()), mOptions.getConnectTimeoutSecond() * 1000);
                //关闭Nagle算法,无论TCP数据报大小,立即发送
                mSocket.setTcpNoDelay(true);
                resolveManager();
                sendBroadcast(IAction.ACTION_CONNECTION_SUCCESS);
                SLog.i("Socket server: " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " connect successful!");
            } catch (Exception e) {
                if (isConnectTimeout) {//超时后不处理Socket异常
                    return;
                }
                SLog.e("Socket server " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " connect failed! error msg:" + e.getMessage());
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, new UnconnectException(e));
                canConnect = true;
            }
        }
    }

    private void resolveManager() throws IOException {
        mPulseManager = new PulseManager(this, mOptions);

        mManager = new IOThreadManager(
                mSocket.getInputStream(),
                mSocket.getOutputStream(),
                mOptions,
                mActionDispatcher);
        mManager.startEngine();
    }

    @Override
    public synchronized void disconnect(Exception exception) {
        if (isDisconnecting) {
            return;
        }
        isDisconnecting = true;
        if (mConnectThread != null && mConnectThread.isAlive()) {
            mConnectThread.interrupt();
            mConnectThread = null;
        }
        if (mPulseManager != null) {
            mPulseManager.dead();
            mPulseManager = null;
        }

        if (exception instanceof ManuallyDisconnectException) {
            if (mReconnectionManager != null) {
                mReconnectionManager.detach();
                SLog.i("ReconnectionManager is detached.");
            }
        }

        String info = mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort();
        DisconnectThread thread = new DisconnectThread(exception, "Disconnect Thread for " + info);
        thread.setDaemon(true);
        thread.start();
    }

    private class DisconnectThread extends Thread {
        private Exception mException;

        public DisconnectThread(Exception exception, String name) {
            super(name);
            mException = exception;
        }

        @Override
        public void run() {
            if (mManager != null) {
                mManager.close(mException);
            }

            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                }
            }

            if (mActionHandler != null) {
                mActionHandler.detach(ConnectionManagerImpl.this);
                mActionHandler = null;
            }

            isDisconnecting = false;
            canConnect = true;
            if (!(mException instanceof UnconnectException) && mSocket != null) {
                mException = mException instanceof ManuallyDisconnectException ? null : mException;
                sendBroadcast(IAction.ACTION_DISCONNECTION, mException);
            }
            mSocket = null;

            if (mException != null) {
                SLog.e("socket is disconnecting because: " + mException.getMessage());
            }
        }
    }


    @Override
    public void disconnect() {
        disconnect(new ManuallyDisconnectException());
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
        if (okOptions == null) {
            return this;
        }
        mOptions = okOptions;
        if (mManager != null) {
            mManager.setOkOptions(mOptions);
        }
        if (mPulseManager != null) {
            mPulseManager.setOkOptions(mOptions);
        }
        return this;
    }

    @Override
    public OkSocketOptions getOption() {
        return mOptions;
    }

    @Override
    public boolean isConnect() {
        if (mSocket == null) {
            return false;
        }

        return mSocket.isConnected() && !mSocket.isClosed();
    }

    @Override
    public boolean isDisconnecting() {
        return isDisconnecting;
    }

    @Override
    public PulseManager getPulseManager() {
        return mPulseManager;
    }

    @Override
    public void setIsConnectionHolder(boolean isHold) {
        mOptions = new OkSocketOptions.Builder(mOptions).setConnectionHolden(isHold).build();
    }

    @Override
    public AbsReconnectionManager getReconnectionManager() {
        return mOptions.getReconnectionManager();
    }
}
