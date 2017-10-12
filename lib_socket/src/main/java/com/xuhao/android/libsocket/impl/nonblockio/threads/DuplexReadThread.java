package com.xuhao.android.libsocket.impl.nonblockio.threads;

import android.content.Context;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.utils.SL;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by xuhao on 2017/5/17.
 */

public class DuplexReadThread extends LoopThread {
    private IStateSender mStateSender;

    private Selector mSelector;

    private SelectionKey mSelectionKey;

    private IReader mReader;

    private DuplexWriteThread mWriteThread;

    public DuplexReadThread(Context context, SelectionKey selectionKey, IReader reader,
            DuplexWriteThread duplexWriteThread, IStateSender stateSender) {
        super(context, "duplex_read_thread");
        this.mStateSender = stateSender;
        this.mSelector = selectionKey.selector();
        this.mSelectionKey = selectionKey;
        this.mReader = reader;
        this.mWriteThread = duplexWriteThread;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        if (mWriteThread.isNeedSend()) {
            try {
                mWriteThread.thread.join(100);
            } catch (InterruptedException e) {
                //ignore;
            }
        }
        int readyChannels = mSelector.select();
        if (readyChannels == 0) {
            return;
        }
        Set<SelectionKey> set = mSelector.selectedKeys();
        synchronized (set) {
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isValid() && key.isReadable() && key.equals(mSelectionKey)) {
                    mReader.read();
                }
                iterator.remove();
            }
        }
    }

    @Override
    protected void loopFinish(Exception e) {
        if (e != null) {
            SL.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
