package com.xuhao.android.server.impl.iocore;

import android.content.Context;

import com.xuhao.android.common.basic.AbsLoopThread;
import com.xuhao.android.common.interfacies.client.io.IReader;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.server.action.IAction;
import com.xuhao.android.server.exceptions.InitiativeDisconnectException;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class ClientReadThread extends AbsLoopThread {
    private IStateSender mClientStateSender;

    private IReader mReader;

    public ClientReadThread(Context context, IReader reader, IStateSender clientStateSender) {
        super(context, "client_read_thread");
        this.mClientStateSender = clientStateSender;
        this.mReader = reader;
    }

    @Override
    protected void beforeLoop() {
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mReader.read();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mReader.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof InitiativeDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
