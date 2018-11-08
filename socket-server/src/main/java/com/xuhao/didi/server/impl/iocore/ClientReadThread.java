package com.xuhao.didi.server.impl.iocore;

import android.content.Context;

import com.xuhao.didi.common.basic.AbsLoopThread;
import com.xuhao.didi.common.common_interfacies.client.io.IReader;
import com.xuhao.didi.common.common_interfacies.dispatcher.IStateSender;
import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.server.action.IAction;
import com.xuhao.didi.server.exceptions.InitiativeDisconnectException;

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
