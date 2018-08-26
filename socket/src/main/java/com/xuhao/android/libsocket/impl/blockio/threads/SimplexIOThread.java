package com.xuhao.android.libsocket.impl.blockio.threads;

import android.content.Context;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.impl.abilities.IWriter;
import com.xuhao.android.libsocket.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.utils.SL;

import java.io.IOException;

/**
 * Created by xuhao on 2017/5/17.
 */

public class SimplexIOThread extends LoopThread {
    private IStateSender mStateSender;

    private IReader mReader;

    private IWriter mWriter;

    private boolean isWrite = false;


    public SimplexIOThread(Context context, IReader reader,
                           IWriter writer, IStateSender stateSender) {
        super(context, "simplex_io_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() throws IOException {
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_START);
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        isWrite = mWriter.write();
        if (isWrite) {
            isWrite = false;
            mReader.read();
        }
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mReader.close();
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SL.e("simplex error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
