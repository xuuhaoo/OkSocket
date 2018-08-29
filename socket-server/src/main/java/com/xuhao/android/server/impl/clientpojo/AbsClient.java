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

    private boolean[] isReady = new boolean[]{false, false};

    public AbsClient(@NonNull Socket socket, @NonNull OkServerOptions okServerOptions) {
        this.mOkServerOptions = okServerOptions;
        this.mSocket = socket;
        this.mInetAddress = mSocket.getInetAddress();
        this.mReaderProtocol = mOkServerOptions.getReaderProtocol();
    }

    @Override
    public String getHostName() {
        return mInetAddress.getCanonicalHostName();
    }

    @Override
    public void setUniqueTag(String uniqueTag) {
        mUniqueTag = uniqueTag;
    }

    @Override
    public String getUniqueTag() {
        if(TextUtils.isEmpty(mUniqueTag)){
            return getHostName();
        }
        return mUniqueTag;
    }

    private void judge(Exception e) {
        if (isReady[0] && isReady[1]) {
            onClientReady();
        } else if (!isReady[0] && !isReady[1]) {
            onClientDead(e);
        }
    }

    @Override
    public final void onClientReadReady() {
        isReady[0] = true;
        judge(null);
    }

    @Override
    public final void onClientWriteReady() {
        isReady[1] = true;
        judge(null);
    }

    @Override
    public final void onClientReadDead(Exception e) {
        isReady[0] = false;
        judge(e);
    }

    @Override
    public final void onClientWriteDead(Exception e) {
        isReady[1] = false;
        judge(e);
    }

    protected abstract void onClientReady();

    protected abstract void onClientDead(Exception e);
}
