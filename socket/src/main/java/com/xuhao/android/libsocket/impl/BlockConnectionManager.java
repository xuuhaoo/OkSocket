package com.xuhao.android.libsocket.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.xuhao.android.libsocket.impl.abilities.IIOManager;
import com.xuhao.android.libsocket.impl.blockio.IOManager;
import com.xuhao.android.libsocket.impl.exceptions.UnconnectException;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.OkSocketSSLConfig;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.connection.AbsReconnectionManager;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.sdk.protocol.DefaultX509ProtocolTrustManager;
import com.xuhao.android.libsocket.utils.SL;

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

public class BlockConnectionManager extends AbsConnectionManager {
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
    private boolean canConnect = true;
    /**
     * 是否正在断开
     */
    private boolean isDisconnecting = false;
    /**
     * 是否连接超时
     */
    private boolean isConnectTimeout = false;
    /**
     * 连接超时处理Task
     */
    private Handler mConnectionTimeout = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                isConnectTimeout = true;
                removeCallbacksAndMessages(null);
                if (mSocket != null && mSocket.isConnected()) {
                    isConnectTimeout = false;
                    return;
                }
                try {
                    mSocket.close();
                } catch (IOException e) {
                    //ignore
                }

                SL.e(mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + "连接超时,终止连接");
                Exception exception = new UnconnectException(mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort
                        () + "连接超时,终止连接");
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, exception);
            }
        }
    };

    protected BlockConnectionManager(Context context, ConnectionInfo info) {
        super(context, info);
        SL.i("block connection init");
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
        isDisconnecting = false;
        if (mConnectionInfo == null) {
            throw new UnconnectException("连接参数为空,检查连接参数");
        }
        if (mActionHandler != null) {
            mActionHandler.detach(this);
        }
        mActionHandler = new SocketActionHandler();
        mActionHandler.attach(this, this);

        if (mConnectionTimeout != null) {
            mConnectionTimeout.removeCallbacksAndMessages(null);
        }
        if (mReconnectionManager != null) {
            mReconnectionManager.detach();
        }
        mReconnectionManager = mOptions.getReconnectionManager();
        if (mReconnectionManager != null) {
            mReconnectionManager.attach(mContext, this);
        }
        mSocket = getSocketByConfig();
        mConnectionTimeout
                .sendMessageDelayed(mConnectionTimeout.obtainMessage(0), mOptions.getConnectTimeoutSecond() * 1000);
        mConnectThread = new ConnectionThread(
                mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " connect thread");
        mConnectThread.setDaemon(true);
        mConnectThread.start();
    }

    @NonNull
    private Socket getSocketByConfig() {
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
                SL.e(e.getMessage());
                return new Socket();
            }

        } else {
            try {
                return factory.createSocket();
            } catch (IOException e) {
                SL.e(e.getMessage());
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
                isConnectTimeout = false;
                SL.i("开始连接 " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " Socket服务器");
                mSocket.connect(new InetSocketAddress(mConnectionInfo.getIp(), mConnectionInfo.getPort()));
                //关闭Nagle算法,无论TCP数据报大小,立即发送
                mSocket.setTcpNoDelay(true);
                mConnectionTimeout.removeCallbacksAndMessages(null);
                resolveManager();
                sendBroadcast(IAction.ACTION_CONNECTION_SUCCESS);
                SL.i("Socket服务器连接成功 " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort());
            } catch (Exception e) {
                if (isConnectTimeout) {//超时后不处理Socket异常
                    return;
                }
                SL.i("连接 " + mConnectionInfo.getIp() + ":" + mConnectionInfo.getPort() + " 出现异常:" + e.getMessage());
                mConnectionTimeout.removeCallbacksAndMessages(null);
                sendBroadcast(IAction.ACTION_CONNECTION_FAILED, new UnconnectException(e));
                canConnect = true;
            }
        }
    }

    private void resolveManager() throws IOException {
        mPulseManager = new PulseManager(this, mOptions);

        mManager = new IOManager(mContext, mSocket.getInputStream(), mSocket.getOutputStream(), mOptions,
                BlockConnectionManager.this);
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
        if (isDisconnecting) {
            return;
        }
        isDisconnecting = true;
        if (mConnectThread != null && mConnectThread.isAlive()) {
            mConnectThread.interrupt();
            mConnectThread = null;
        }
        if (mConnectionTimeout != null) {
            mConnectionTimeout.removeCallbacksAndMessages(null);
        }
        if (mPulseManager != null) {
            mPulseManager.dead();
            mPulseManager = null;
        }
        if (mSocket != null) {
            try {
                if (mSocket.getInputStream() != null) {
                    mSocket.getInputStream().close();
                }
            } catch (Exception e) {
            }
            try {
                if (mSocket.getOutputStream() != null) {
                    mSocket.getOutputStream().close();
                }
            } catch (Exception e) {
            }

            try {
                mSocket.close();
            } catch (IOException e) {
            }
            mSocket = null;
        }
        if (mManager != null) {
            mManager.close();
        }

        if (!(exception instanceof UnconnectException)) {
            sendBroadcast(IAction.ACTION_DISCONNECTION, exception);
        }

        if (mActionHandler != null) {
            mActionHandler.detach(BlockConnectionManager.this);
            mActionHandler = null;
        }
        if (exception != null) {
            exception.printStackTrace();
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
        EnvironmentalManager.getIns().setOkOptions(mOptions);
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

        return mSocket.isConnected() && !mSocket.isClosed() && netIsAvailable();
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
