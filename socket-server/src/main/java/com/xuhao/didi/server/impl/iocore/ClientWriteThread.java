package com.xuhao.didi.server.impl.iocore;

import android.content.Context;

import com.xuhao.didi.common.basic.AbsLoopThread;
import com.xuhao.didi.common.common_interfacies.client.io.IWriter;
import com.xuhao.didi.common.common_interfacies.dispatcher.IStateSender;
import com.xuhao.didi.core.utils.SLog;
import com.xuhao.didi.server.action.IAction;
import com.xuhao.didi.server.exceptions.InitiativeDisconnectException;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class ClientWriteThread extends AbsLoopThread {
    private IStateSender mClientStateSender;

    private IWriter mWriter;

    public ClientWriteThread(Context context, IWriter writer, IStateSender clientStateSender) {
        super(context, "client_write_thread");
        this.mClientStateSender = clientStateSender;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() {
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_WRITE_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mWriter.write();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof InitiativeDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }
        mClientStateSender.sendBroadcast(IAction.Client.ACTION_WRITE_THREAD_SHUTDOWN, e);
    }
}
