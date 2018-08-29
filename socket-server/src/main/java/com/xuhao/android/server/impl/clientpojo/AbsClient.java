package com.xuhao.android.server.impl.clientpojo;

import android.support.annotation.NonNull;

import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.server.impl.OkServerOptions;

import java.net.InetAddress;
import java.net.Socket;

public abstract class AbsClient implements IClient {
    protected IReaderProtocol mReaderProtocol;

    protected OkServerOptions mOkServerOptions;

    protected InetAddress mInetAddress;

    protected Socket mSocket;

    protected String mUniqueTag;

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
        return mUniqueTag;
    }

    @Override
    public void setReaderProtocol(@NonNull IReaderProtocol protocol) {
        mReaderProtocol = protocol;
    }

}
