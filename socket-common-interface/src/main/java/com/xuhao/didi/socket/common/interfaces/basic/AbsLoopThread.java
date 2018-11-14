package com.xuhao.didi.socket.common.interfaces.basic;

import com.xuhao.didi.core.utils.SLog;

/**
 * Created by xuhao on 15/6/18.
 */
public abstract class AbsLoopThread implements Runnable {
    public volatile Thread thread = null;

    protected volatile String threadName = "";

    private volatile boolean isStop = false;

    private volatile boolean isShutdown = true;

    private volatile Exception ioException = null;

    private volatile long loopTimes = 0;

    public AbsLoopThread() {
        isStop = true;
        threadName = this.getClass().getSimpleName();
    }

    public AbsLoopThread(String name) {
        isStop = true;
        threadName = name;
    }

    public synchronized void start() {
        if (isStop) {
            thread = new Thread(this, threadName);
            isStop = false;
            loopTimes = 0;
            thread.start();
            SLog.w(threadName + " is starting");
        }
    }

    @Override
    public final void run() {
        try {
            isShutdown = false;
            beforeLoop();
            while (!isStop) {
                this.runInLoopThread();
                loopTimes++;
            }
        } catch (Exception e) {
            if (ioException == null) {
                ioException = e;
            }
        } finally {
            isShutdown = true;
            this.loopFinish(ioException);
            ioException = null;
            SLog.w(threadName + " is shutting down");
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

    public synchronized void shutdown(Exception e) {
        this.ioException = e;
        shutdown();
    }

    public boolean isShutdown() {
        return isShutdown;
    }

}
