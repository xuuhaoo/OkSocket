package com.xuhao.didi.socket.server.impl.clientpojo;

import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.utils.TextUtils;
import com.xuhao.didi.socket.server.action.ClientActionDispatcher;
import com.xuhao.didi.socket.server.impl.OkServerOptions;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsClient implements IClient, ClientActionDispatcher.ClientActionListener {
    protected IReaderProtocol mReaderProtocol;

    protected OkServerOptions mOkServerOptions;

    protected InetAddress mInetAddress;

    protected Socket mSocket;

    protected String mUniqueTag;

    private volatile boolean isCallDead;

    private volatile boolean isCallReady;

    private List<OriginalData> mCacheForNotPrepare = new ArrayList<>();

    public AbsClient(Socket socket, OkServerOptions okServerOptions) {
        this.mOkServerOptions = okServerOptions;
        this.mSocket = socket;
        this.mInetAddress = mSocket.getInetAddress();
        this.mReaderProtocol = mOkServerOptions.getReaderProtocol();
        mUniqueTag = getHostIp() + "-" + System.currentTimeMillis() + "-" + System.nanoTime() +"-" + mSocket.getPort();
    }

    @Override
    public String getHostIp() {
        return mInetAddress.getHostAddress();
    }

    @Override
    public String getHostName() {
        return mInetAddress.getCanonicalHostName();
    }

    @Override
    public String getUniqueTag() {
        if (TextUtils.isEmpty(mUniqueTag)) {
            return getHostIp();
        }
        return mUniqueTag;
    }

    @Override
    public final void onClientReadReady() {
        synchronized (this) {
            if (!isCallReady) {
                onClientReady();
                isCallDead = false;
                isCallReady = true;
            }
        }
    }

    @Override
    public void onClientWriteReady() {
        synchronized (this) {
            if (!isCallReady) {
                onClientReady();
                isCallDead = false;
                isCallReady = true;
            }
        }
    }

    @Override
    public final void onClientReadDead(Exception e) {
        synchronized (this) {
            if (!isCallDead) {
                onClientDead(e);
                isCallDead = true;
                isCallReady = false;
            }
        }
    }

    @Override
    public final void onClientWriteDead(Exception e) {
        synchronized (this) {
            if (!isCallDead) {
                onClientDead(e);
                isCallDead = true;
                isCallReady = false;
            }
        }
    }

    protected abstract void onClientReady();

    protected abstract void onClientDead(Exception e);
}
