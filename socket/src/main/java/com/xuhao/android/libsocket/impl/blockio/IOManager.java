package com.xuhao.android.libsocket.impl.blockio;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.libsocket.impl.LoopThread;
import com.xuhao.android.libsocket.impl.abilities.IIOManager;
import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.impl.abilities.IWriter;
import com.xuhao.android.libsocket.impl.blockio.io.ReaderImpl;
import com.xuhao.android.libsocket.impl.blockio.io.WriterImpl;
import com.xuhao.android.libsocket.impl.blockio.threads.DuplexReadThread;
import com.xuhao.android.libsocket.impl.blockio.threads.DuplexWriteThread;
import com.xuhao.android.libsocket.impl.blockio.threads.SimplexIOThread;
import com.xuhao.android.libsocket.impl.exceptions.ManuallyDisconnectException;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.protocol.IHeaderProtocol;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.utils.SL;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xuhao on 2017/5/31.
 */

public class IOManager implements IIOManager {

    private Context mContext;

    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private OkSocketOptions mOkOptions;

    private IStateSender mSender;

    private IReader mReader;

    private IWriter mWriter;

    private LoopThread mSimplexThread;

    private DuplexReadThread mDuplexReadThread;

    private DuplexWriteThread mDuplexWriteThread;

    private OkSocketOptions.IOThreadMode mCurrentThreadMode;

    public IOManager(@NonNull Context context,
                     @NonNull InputStream inputStream,
                     @NonNull OutputStream outputStream,
                     @NonNull OkSocketOptions okOptions,
                     @NonNull IStateSender stateSender) {
        mContext = context;
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mOkOptions = okOptions;
        mSender = stateSender;
        initIO();
    }

    private void initIO() {
        assertHeaderProtocolNotEmpty();
        mReader = new ReaderImpl(mInputStream, mSender);
        mWriter = new WriterImpl(mOutputStream, mSender);
    }

    @Override
    public void resolve() {
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        //初始化读写工具类
        mReader.setOption(mOkOptions);
        mWriter.setOption(mOkOptions);
        switch (mOkOptions.getIOThreadMode()) {
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

    private void duplex() {
        shutdownAllThread(null);
        mDuplexWriteThread = new DuplexWriteThread(mContext, mWriter, mSender);
        mDuplexReadThread = new DuplexReadThread(mContext, mReader, mSender);
        mDuplexWriteThread.start();
        mDuplexReadThread.start();
    }

    private void simplex() {
        shutdownAllThread(null);
        mSimplexThread = new SimplexIOThread(mContext, mReader, mWriter, mSender);
        mSimplexThread.start();
    }

    private void shutdownAllThread(Exception e) {
        if (mSimplexThread != null) {
            mSimplexThread.shutdown(e);
            mSimplexThread = null;
        }
        if (mDuplexReadThread != null) {
            mDuplexReadThread.shutdown(e);
            mDuplexReadThread = null;
        }
        if (mDuplexWriteThread != null) {
            mDuplexWriteThread.shutdown(e);
            mDuplexWriteThread = null;
        }
    }

    @Override
    public void setOkOptions(OkSocketOptions options) {
        mOkOptions = options;
        if (mCurrentThreadMode == null) {
            mCurrentThreadMode = mOkOptions.getIOThreadMode();
        }
        assertTheThreadModeNotChanged();
        assertHeaderProtocolNotEmpty();

        mWriter.setOption(mOkOptions);
        mReader.setOption(mOkOptions);
    }

    @Override
    public void send(ISendable sendable) {
        mWriter.offer(sendable);
    }

    @Override
    public void close() {
        close(new ManuallyDisconnectException());
    }

    @Override
    public void close(Exception e) {
        shutdownAllThread(e);
        mCurrentThreadMode = null;
    }

    private void assertHeaderProtocolNotEmpty() {
        IHeaderProtocol protocol = mOkOptions.getHeaderProtocol();
        if (protocol == null) {
            throw new IllegalArgumentException("The header protocol can not be Null.");
        }

        if (protocol.getHeaderLength() == 0) {
            throw new IllegalArgumentException("The header length can not be zero.");
        }
    }

    private void assertTheThreadModeNotChanged() {
        if (mOkOptions.getIOThreadMode() != mCurrentThreadMode) {
            throw new IllegalArgumentException("can't hot change iothread mode from " + mCurrentThreadMode + " to "
                    + mOkOptions.getIOThreadMode() + " in blocking io manager");
        }
    }

}
