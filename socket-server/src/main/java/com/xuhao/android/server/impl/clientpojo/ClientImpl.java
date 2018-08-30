package com.xuhao.android.server.impl.clientpojo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.common.interfacies.server.IClientIOCallback;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.server.action.ClientActionDispatcher;
import com.xuhao.android.server.action.IAction;
import com.xuhao.android.server.exceptions.CacheException;
import com.xuhao.android.server.impl.OkServerOptions;
import com.xuhao.android.server.impl.iocore.ClientIOManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientImpl extends AbsClient {

    private volatile boolean isDead;

    private volatile boolean isReadEngineStarted;

    private ClientIOManager mIOManager;

    private IStateSender mActionDispatcher;

    private ClientPoolImpl mClientPool;

    private IStateSender mServerStateSender;

    private List<IClientIOCallback> mCallbackList = new ArrayList<>();

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
            synchronized (mIOManager) {
                mIOManager.startWriteEngin();
            }
        }
    }

    @Override
    public void disconnect(Exception e) {
        if (mIOManager != null) {
            synchronized (mIOManager) {
                mIOManager.close(e);
            }
        } else {
            onClientDead(e);
        }
        try {
            synchronized (mSocket) {
                mSocket.close();
            }
        } catch (IOException e1) {
        }
        removeAllIOCallback();
        isReadEngineStarted = false;
    }

    @Override
    public void disconnect() {
        if (mIOManager != null) {
            synchronized (mIOManager) {
                mIOManager.close();
            }
        } else {
            onClientDead(null);
        }
        try {
            synchronized (mSocket) {
                mSocket.close();
            }
        } catch (IOException e1) {
        }
        removeAllIOCallback();
        isReadEngineStarted = false;
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
        disconnect(e);
        mServerStateSender.sendBroadcast(IAction.Server.ACTION_CLIENT_DISCONNECTED, this);
        synchronized (this) {
            isDead = true;
        }
    }

    @Override
    public void setReaderProtocol(@NonNull IReaderProtocol protocol) {
        if (mIOManager != null) {
            synchronized (mIOManager) {
                OkServerOptions.Builder builder = new OkServerOptions.Builder(mOkServerOptions);
                builder.setReaderProtocol(protocol);
                mOkServerOptions = builder.build();
                mIOManager.setOkOptions(mOkServerOptions);
            }
        }
    }

    @Override
    public void addIOCallback(IClientIOCallback clientIOCallback) {
        synchronized (mCallbackList) {
            mCallbackList.add(clientIOCallback);
            if (!isReadEngineStarted) {
                isReadEngineStarted = true;
                startRead();
            }
        }
    }

    @Override
    public void removeIOCallback(IClientIOCallback clientIOCallback) {
        synchronized (mCallbackList) {
            mCallbackList.remove(clientIOCallback);
        }
    }

    @Override
    public void removeAllIOCallback() {
        synchronized (mCallbackList) {
            mCallbackList.clear();
        }
    }

    private void startRead() {
        if (isDead) {
            return;
        }
        if (mIOManager != null) {
            mIOManager.startReadEngine();
        }
    }

    @Override
    public void onClientRead(OriginalData originalData) {
        List<IClientIOCallback> list = new ArrayList<>();
        list.addAll(mCallbackList);

        for (IClientIOCallback clientIOCallback : list) {
            try {
                clientIOCallback.onClientRead(originalData, this, mClientPool);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClientWrite(ISendable sendable) {
        List<IClientIOCallback> list = new ArrayList<>();
        list.addAll(mCallbackList);

        for (IClientIOCallback clientIOCallback : list) {
            try {
                clientIOCallback.onClientWrite(sendable, this, mClientPool);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
