package com.xuhao.android.libsocket.impl.nonblockio;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IIOManager;
import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.impl.abilities.IWriter;
import com.xuhao.android.libsocket.impl.nonblockio.nio.Reader;
import com.xuhao.android.libsocket.impl.nonblockio.nio.Writer;
import com.xuhao.android.libsocket.impl.nonblockio.threads.DuplexReadThread;
import com.xuhao.android.libsocket.impl.nonblockio.threads.DuplexWriteThread;
import com.xuhao.android.libsocket.impl.nonblockio.threads.SimplexIOThread;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.utils.SL;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by xuhao on 2017/5/16.
 */

public class IOManager implements IIOManager {
    private Context mContext;
    /**
     * 单工线程
     */
    private LoopThread mSimplexThread;
    /**
     * 双工读线程
     */
    private DuplexReadThread mDuplexReadThread;
    /**
     * 双工写线程
     */
    private DuplexWriteThread mDuplexWriteThread;
    /**
     * 读取处理类
     */
    private IReader mReader;
    /**
     * 写出处理类
     */
    private IWriter mWriter;
    /**
     * 相关参配
     */
    private OkSocketOptions mOkOptions;
    /**
     * 当前线程模式
     */
    private OkSocketOptions.IOThreadMode mCurrentThreadMode;
    /**
     * 状态发送机
     */
    private IStateSender mStateSender;
    /**
     * 通道key标识
     */
    private SelectionKey mSelectionKey;

    public IOManager(@NonNull Context context,
            @NonNull SelectionKey selectionKey,
            @NonNull OkSocketOptions okOptions,
            @NonNull IStateSender stateSender) {
        mContext = context;
        mOkOptions = okOptions;
        mStateSender = stateSender;
        mSelectionKey = selectionKey;
        initIO();
    }

    private void initIO() {
        mReader = new Reader((SocketChannel) mSelectionKey.channel(), mStateSender);
        mWriter = new Writer((SocketChannel) mSelectionKey.channel(), mStateSender);
    }

    @Override
    public void resolve() {
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        //初始化读写工具类
        mReader.setOption(mOkOptions);
        mWriter.setOption(mOkOptions);
        switch (mCurrentThreadMode) {
            case DUPLEX:
                SL.e("DUPLEX is processing");
                duplex();
                break;
            case SIMPLEX:
                SL.e("SIMPLEX is processing");
                simplex();
                break;
            default:
                throw new RuntimeException("未定义的线程模式");
        }
    }

    @Override
    public void close() {
        shutdownAllThread();
    }

    /**
     * 单工
     */
    private void simplex() {
        shutdownAllThread();
        mSimplexThread = new SimplexIOThread(mContext, mSelectionKey, mReader, mWriter, mStateSender);
        mSimplexThread.start();
    }

    /**
     * 双工
     */
    private void duplex() {
        shutdownAllThread();
        mDuplexWriteThread = new DuplexWriteThread(mContext, mSelectionKey, mWriter, mStateSender);
        mDuplexReadThread = new DuplexReadThread(mContext, mSelectionKey, mReader, mDuplexWriteThread, mStateSender);
        mDuplexWriteThread.start();
        mDuplexReadThread.start();
    }

    private void shutdownAllThread() {
        if (mSimplexThread != null) {
            mSimplexThread.shutdown();
            mSimplexThread = null;
        }
        if (mDuplexReadThread != null) {
            mDuplexReadThread.shutdown();
            mDuplexReadThread = null;
        }
        if (mDuplexWriteThread != null) {
            mDuplexWriteThread.shutdown();
            mDuplexWriteThread = null;
        }
    }

    public void setOkOptions(OkSocketOptions okOptions) {
        mOkOptions = okOptions;
        if (mOkOptions.getIOThreadMode() != mCurrentThreadMode) {
            resolve();
        } else {
            mWriter.setOption(okOptions);
            mReader.setOption(okOptions);
        }
    }

    @Override
    public void send(ISendable sendable) {
        if (mWriter != null) {
            mWriter.offer(sendable);
        }
    }
}
