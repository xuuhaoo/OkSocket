package com.xuhao.didi.common.basic;

import android.content.Context;
import android.os.Process;

import com.xuhao.didi.common.utils.SLog;

/**
 * Created by xuhao on 15/6/18.
 */
public abstract class AbsLoopThread implements Runnable {
    public Thread thread = null;

    protected String threadName = "";

    protected Context context = null;

    private boolean isStop = false;

    private Exception ioException = null;

    private long loopTimes = 0;

    public AbsLoopThread(Context context) {
        this.context = context;
        isStop = true;
        threadName = this.getClass().getSimpleName();
    }

    public AbsLoopThread(Context context, String name) {
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
            SLog.w(threadName + " is starting");
        }
    }

    @Override
    public final void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            beforeLoop();
            while (!isStop) {
                this.runInLoopThread();
                loopTimes++;
            }
        } catch (Exception e) {
            if (ioException == null) {
                e.printStackTrace();
                ioException = e;
            }
        } finally {
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
        return isStop;
    }

}
