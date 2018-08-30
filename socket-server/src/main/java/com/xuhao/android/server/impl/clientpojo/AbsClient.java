package com.xuhao.android.server.impl.clientpojo;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.server.action.ClientActionDispatcher;
import com.xuhao.android.server.impl.OkServerOptions;

import java.net.InetAddress;
import java.net.Socket;

public abstract class AbsClient implements IClient, ClientActionDispatcher.ClientActionListener {
    protected IReaderProtocol mReaderProtocol;

    protected OkServerOptions mOkServerOptions;

    protected InetAddress mInetAddress;

    protected Socket mSocket;

    protected String mUniqueTag;

    private boolean isCallDead;

    private boolean[] isReady = new boolean[]{false, false};

    public AbsClient(@NonNull Socket socket, @NonNull OkServerOptions okServerOptions) {
        this.mOkServerOptions = okServerOptions;
        this.mSocket = socket;
        this.mInetAddress = mSocket.getInetAddress();
        this.mReaderProtocol = mOkServerOptions.getReaderProtocol();
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
    public void setUniqueTag(String uniqueTag) {
        synchronized (AbsClient.class) {
            mUniqueTag = uniqueTag;
        }
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
        synchronized (isReady) {
            isReady[0] = true;
            if (isReady[0] && isReady[1]) {
                onClientReady();
                isCallDead = false;
            }
        }
    }

    @Override
    public final void onClientWriteReady() {
        synchronized (isReady) {
            isReady[1] = true;
            if (isReady[0] && isReady[1]) {
                onClientReady();
                isCallDead = false;
            }
        }
    }

    @Override
    public final void onClientReadDead(Exception e) {
        synchronized (isReady) {
            isReady[0] = false;
            if ((!isReady[0] || !isReady[1]) && !isCallDead) {
                onClientDead(e);
                isCallDead = true;
            }
        }
    }

    @Override
    public final void onClientWriteDead(Exception e) {
        synchronized (isReady) {
            isReady[1] = false;
            if ((!isReady[0] || !isReady[1]) && !isCallDead) {
                onClientDead(e);
                isCallDead = true;
            }
        }
    }

    protected abstract void onClientReady();

    protected abstract void onClientDead(Exception e);
}
