package com.xuhao.android.server.impl.clientpojo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.server.IClient;
import com.xuhao.android.server.action.ClientActionDispatcher;
import com.xuhao.android.server.impl.OkServerOptions;
import com.xuhao.android.server.impl.iocore.ClientIOManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends AbsClient {

    private ClientIOManager mIOManager;

    private ClientActionDispatcher mActionDispatcher;

    public Client(Context context, @NonNull Socket socket, @NonNull OkServerOptions okServerOptions) throws IOException {
        super(socket, okServerOptions);
//        mActionDispatcher = new ClientActionDispatcher();

        initIOManager(context);
    }

    private void initIOManager(Context context) throws IOException {
        InputStream inputStream = mSocket.getInputStream();
        OutputStream outputStream = mSocket.getOutputStream();
        mIOManager = new ClientIOManager(context, inputStream, outputStream, mOkServerOptions, mActionDispatcher);
    }

    @Override
    public void disconnect(Exception e) {
        mIOManager.close(e);
    }

    @Override
    public void disconnect() {
        mIOManager.close();
    }

    @Override
    public IClient send(ISendable sendable) {
        mIOManager.send(sendable);
        return this;
    }
}
