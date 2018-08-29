package com.xuhao.android.server.impl.clientpojo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.server.action.ClientActionDispatcher;
import com.xuhao.android.server.action.IAction;
import com.xuhao.android.server.exceptions.CacheException;
import com.xuhao.android.server.impl.OkServerOptions;
import com.xuhao.android.server.impl.iocore.ClientIOManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientImpl extends AbsClient {

    private boolean isDead;

    private ClientIOManager mIOManager;

    private IStateSender mActionDispatcher;

    private ClientPoolImpl mClientPool;

    private IStateSender mServerStateSender;

    public ClientImpl(Context context,
                      @NonNull Socket socket,
                      @NonNull OkServerOptions okServerOptions) {
        super(socket, okServerOptions);
        mActionDispatcher = new ClientActionDispatcher(this);

        try {
            initIOManager(context);
        } catch (IOException e) {
            disconnect(e);
        }
    }

    public void setClientPool(ClientPoolImpl clientPool) {
        mClientPool = clientPool;
    }

    public void setServerStateSender(IStateSender serverStateSender) {
        mServerStateSender = serverStateSender;
    }

    private void initIOManager(Context context) throws IOException {
        InputStream inputStream = mSocket.getInputStream();
        OutputStream outputStream = mSocket.getOutputStream();
        mIOManager = new ClientIOManager(context, inputStream, outputStream, mOkServerOptions, mActionDispatcher);
    }

    public void startIOEngine() {
        if (mIOManager != null) {
            mIOManager.startEngine();
        }
    }

    @Override
    public void disconnect(Exception e) {
        if (mIOManager != null) {
            mIOManager.close(e);
        } else {
            onClientDead(e);
        }
        try {
            mSocket.close();
        } catch (IOException e1) {
        }
    }

    @Override
    public void disconnect() {
        if (mIOManager != null) {
            mIOManager.close();
        } else {
            onClientDead(null);
        }
        try {
            mSocket.close();
        } catch (IOException e1) {
        }
    }

    @Override
    public IClient send(ISendable sendable) {
        if (mIOManager != null) {
            mIOManager.send(sendable);
        }
        return this;
    }

    @Override
    protected void onClientReady() {
        if (isDead) {
            return;
        }
        mClientPool.cache(this);
        mServerStateSender.sendBroadcast(IAction.Server.ACTION_CLIENT_CONNECTED, this);
    }

    @Override
    protected void onClientDead(Exception e) {
        if (isDead) {
            return;
        }
        if (!(e instanceof CacheException)) {
            mClientPool.unCache(this);
        }
        if (e != null) {
            e.printStackTrace();
        }
        mServerStateSender.sendBroadcast(IAction.Server.ACTION_CLIENT_DISCONNECTED, this);
        isDead = true;
    }

    @Override
    public void setReaderProtocol(@NonNull IReaderProtocol protocol) {
        if (mIOManager != null) {
            OkServerOptions.Builder builder = new OkServerOptions.Builder(mOkServerOptions);
            builder.setReaderProtocol(protocol);
            mOkServerOptions = builder.build();
            mIOManager.setOkOptions(mOkServerOptions);
        }
    }

    @Override
    public void onClientRead(OriginalData originalData) {

    }

    @Override
    public void onClientWrite(ISendable sendable) {

    }
}
