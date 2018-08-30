package com.xuhao.android.server.impl.iocore;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xuhao.android.common.interfacies.IIOManager;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.interfacies.client.io.IReader;
import com.xuhao.android.common.interfacies.client.io.IWriter;
import com.xuhao.android.common.interfacies.client.msg.ISendable;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.common.utils.SPIUtils;
import com.xuhao.android.server.exceptions.InitiativeDisconnectException;
import com.xuhao.android.server.impl.OkServerOptions;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientIOManager implements IIOManager<OkServerOptions> {
    private Context mContext;

    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private OkServerOptions mOptions;

    private IStateSender mClientStateSender;

    private IReader mReader;

    private IWriter mWriter;

    private ClientReadThread mClientReadThread;

    private ClientWriteThread mClientWriteThread;

    public ClientIOManager(@NonNull Context context,
                           @NonNull InputStream inputStream,
                           @NonNull OutputStream outputStream,
                           @NonNull OkServerOptions okOptions,
                           @NonNull IStateSender clientStateSender) {
        mContext = context;
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mOptions = okOptions;
        mClientStateSender = clientStateSender;
        initIO();
    }

    private void initIO() {
        assertHeaderProtocolNotEmpty();
        mReader = SPIUtils.load(IReader.class);
        mWriter = SPIUtils.load(IWriter.class);

        if (mReader == null || mWriter == null) {
            throw new IllegalStateException("this library depends on <com.tonystark.android:socket:3.X>");
        }
        setOkOptions(mOptions);

        mReader.initialize(mInputStream, mClientStateSender);
        mWriter.initialize(mOutputStream, mClientStateSender);
    }

    @Override
    public void startEngine() {
        shutdownAllThread(null);

        mClientWriteThread = new ClientWriteThread(mContext, mWriter, mClientStateSender);
        mClientReadThread = new ClientReadThread(mContext, mReader, mClientStateSender);

        mClientWriteThread.start();
        mClientReadThread.start();
    }

    public void startReadEngine() {
        if (mClientReadThread != null) {
            mClientReadThread.shutdown();
            mClientReadThread = null;
        }
        mClientReadThread = new ClientReadThread(mContext, mReader, mClientStateSender);
        mClientReadThread.start();
    }

    public void startWriteEngin() {
        if (mClientWriteThread != null) {
            mClientWriteThread.shutdown();
            mClientWriteThread = null;
        }
        mClientWriteThread = new ClientWriteThread(mContext, mWriter, mClientStateSender);
        mClientWriteThread.start();
    }

    private void shutdownAllThread(Exception e) {
        if (mClientReadThread != null) {
            mClientReadThread.shutdown(e);
            mClientReadThread = null;
        }
        if (mClientWriteThread != null) {
            mClientWriteThread.shutdown(e);
            mClientWriteThread = null;
        }
    }

    @Override
    public void setOkOptions(OkServerOptions options) {
        mOptions = options;

        assertHeaderProtocolNotEmpty();
        if (mWriter != null && mReader != null) {
            mWriter.setOption(mOptions);
            mReader.setOption(mOptions);
        }
    }

    @Override
    public void send(ISendable sendable) {
        mWriter.offer(sendable);
    }

    @Override
    public void close() {
        close(new InitiativeDisconnectException());
    }

    @Override
    public void close(Exception e) {
        shutdownAllThread(e);
    }

    private void assertHeaderProtocolNotEmpty() {
        IReaderProtocol protocol = mOptions.getReaderProtocol();
        if (protocol == null) {
            throw new IllegalArgumentException("The reader protocol can not be Null.");
        }

        if (protocol.getHeaderLength() == 0) {
            throw new IllegalArgumentException("The header length can not be zero.");
        }
    }
}
