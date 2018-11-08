package com.xuhao.didi.libsocket.impl.client.iothreads;

import android.content.Context;

import com.xuhao.didi.common.basic.AbsLoopThread;
import com.xuhao.didi.common.interfacies.client.io.IReader;
import com.xuhao.didi.common.interfacies.dispatcher.IStateSender;
import com.xuhao.didi.common.utils.SLog;
import com.xuhao.didi.libsocket.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.didi.libsocket.sdk.client.action.IAction;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class DuplexReadThread extends AbsLoopThread {
    private IStateSender mStateSender;

    private IReader mReader;

    public DuplexReadThread(Context context, IReader reader, IStateSender stateSender) {
        super(context, "duplex_read_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_START);
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
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
