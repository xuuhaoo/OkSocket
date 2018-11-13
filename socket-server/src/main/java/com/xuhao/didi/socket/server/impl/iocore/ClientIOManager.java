package com.xuhao.didi.socket.server.impl.iocore;

import com.xuhao.didi.core.iocore.ReaderImpl;
import com.xuhao.didi.core.iocore.WriterImpl;
import com.xuhao.didi.core.iocore.interfaces.IReader;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.iocore.interfaces.IStateSender;
import com.xuhao.didi.core.iocore.interfaces.IWriter;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.IIOManager;
import com.xuhao.didi.socket.server.exceptions.InitiativeDisconnectException;
import com.xuhao.didi.socket.server.impl.OkServerOptions;

import java.io.InputStream;
import java.io.OutputStream;

public class ClientIOManager implements IIOManager<OkServerOptions> {
    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private OkServerOptions mOptions;

    private IStateSender mClientStateSender;

    private IReader mReader;

    private IWriter mWriter;

    private ClientReadThread mClientReadThread;

    private ClientWriteThread mClientWriteThread;

    public ClientIOManager(
            InputStream inputStream,
            OutputStream outputStream,
            OkServerOptions okOptions,
            IStateSender clientStateSender) {
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mOptions = okOptions;
        mClientStateSender = clientStateSender;
        initIO();
    }

    private void initIO() {
        assertHeaderProtocolNotEmpty();
        mReader = new ReaderImpl();
        mWriter = new WriterImpl();

        setOkOptions(mOptions);

        mReader.initialize(mInputStream, mClientStateSender);
        mWriter.initialize(mOutputStream, mClientStateSender);
    }

    @Override
    public void startEngine() {
        // do nothing
    }

    public void startReadEngine() {
        if (mClientReadThread != null) {
            mClientReadThread.shutdown();
            mClientReadThread = null;
        }
        mClientReadThread = new ClientReadThread(mReader, mClientStateSender);
        mClientReadThread.start();
    }

    public void startWriteEngine() {
        if (mClientWriteThread != null) {
            mClientWriteThread.shutdown();
            mClientWriteThread = null;
        }
        mClientWriteThread = new ClientWriteThread(mWriter, mClientStateSender);
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
