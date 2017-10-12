package com.xuhao.android.libsocket.impl.nonblockio.threads;

import android.content.Context;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.impl.abilities.IWriter;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.utils.SL;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by xuhao on 2017/5/17.
 */

public class SimplexIOThread extends LoopThread {
    private IStateSender mStateSender;

    private Selector mSelector;

    private SelectionKey mSelectionKey;

    private IReader mReader;

    private IWriter mWriter;

    private boolean isWrite = false;


    public SimplexIOThread(Context context, SelectionKey selectionKey, IReader reader,
            IWriter writer, IStateSender stateSender) {
        super(context, "simplex_io_thread");
        this.mStateSender = stateSender;
        this.mSelector = selectionKey.selector();
        this.mSelectionKey = selectionKey;
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
        int readyChannels = mSelector.select();
        if (readyChannels == 0) {
            return;
        }
        Iterator<SelectionKey> iterator = mSelector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            if (key.isValid() && key.isWritable() && key.equals(mSelectionKey)) {
                if (!isWrite) {
                    isWrite = mWriter.write();
                }
            }
            if (key.isValid() && key.isReadable() && key.equals(mSelectionKey)) {
                if (isWrite) {
                    mReader.read();
                    isWrite = false;
                }
            }
        }
    }

    @Override
    protected void loopFinish(Exception e) {
        if (e != null) {
            SL.e("simplex error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
