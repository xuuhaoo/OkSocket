package com.xuhao.android.libsocket.impl;

import android.content.Context;

import com.xuhao.android.libsocket.utils.SL;

/**
 * Created by xuhao on 15/6/18.
 */
public abstract class LoopThread implements Runnable {
    public Thread thread = null;

    protected String threadName = "";

    protected Context context = null;

    private boolean isStop = false;

    private Exception ioException = null;

    private long loopTimes = 0;

    public LoopThread(Context context) {
        this.context = context;
        isStop = true;
        threadName = this.getClass().getSimpleName();
    }

    public LoopThread(Context context, String name) {
        this.context = context;
        isStop = true;
        threadName = name;
    }

    public synchronized void start() {
        if (isStop) {
            thread = new Thread(this, threadName);
            isStop = false;
            loopTimes = 0;
            thread.start();
            SL.i(threadName + " is starting");
        }
    }

    @Override
    public final void run() {
        try {
            beforeLoop();
            while (!isStop) {
                this.runInLoopThread();
                loopTimes++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ioException = e;
        } finally {
            this.loopFinish(ioException);
            ioException = null;
            SL.i(threadName + " is shutting down");
        }
    }

    public long getLoopTimes() {
        return loopTimes;
    }

    public String getThreadName() {
        return threadName;
    }

    protected void beforeLoop() throws Exception {

    }

    protected abstract void runInLoopThread() throws Exception;

    protected abstract void loopFinish(Exception e);

    public synchronized void shutdown() {
        if (thread != null && !isStop) {
            isStop = true;
            thread.interrupt();
            thread = null;
        }
    }

    protected synchronized void shutdownWithException(Exception e) {
        this.ioException = e;
        shutdown();
    }

    public boolean isShutdown() {
        return isStop;
    }

}
