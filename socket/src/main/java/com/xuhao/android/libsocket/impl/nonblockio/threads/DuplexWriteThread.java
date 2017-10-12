package com.xuhao.android.libsocket.impl.nonblockio.threads;

import android.content.Context;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IWriter;
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

public class DuplexWriteThread extends LoopThread {
    private IStateSender mStateSender;

    private Selector mSelector;

    private SelectionKey mSelectionKey;

    private IWriter mWriter;

    public DuplexWriteThread(Context context, SelectionKey selectionKey, IWriter writer,
            IStateSender stateSender) {
        super(context, "duplex_write_thread");
        this.mStateSender = stateSender;
        this.mSelector = selectionKey.selector();
        this.mSelectionKey = selectionKey;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        int readyChannels = mSelector.select();
        if (readyChannels == 0) {
            return;
        }
        Set<SelectionKey> set = mSelector.selectedKeys();
        synchronized (set) {
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isValid() && key.isWritable() && key.equals(mSelectionKey)) {
                    mWriter.write();
                }
                iterator.remove();
            }
        }
    }

    @Override
    protected void loopFinish(Exception e) {
        if (e != null) {
            SL.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
    }

    public boolean isNeedSend() {
        if (mWriter != null) {
            int size = mWriter.queueSize();
            if (size >= 3) {//需要输出了
                return true;
            }
        }
        return false;
    }
}
