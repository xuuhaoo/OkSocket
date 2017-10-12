package com.xuhao.android.libsocket.impl.nonblockio.nio;

import com.xuhao.android.libsocket.impl.abilities.IWriter;
import com.xuhao.android.libsocket.impl.exceptions.WriteException;
import com.xuhao.android.libsocket.interfaces.IPulseSendable;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.utils.BytesUtils;
import com.xuhao.android.libsocket.utils.SL;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuhao on 2017/5/16.
 */

public class Writer implements IWriter {
    private IStateSender mStateSender;

    private OkSocketOptions mOkOptions;

    private List<ISendable> mQueue = new ArrayList<>();

    private SocketChannel mChannel;

    public Writer(SocketChannel channel, IStateSender stateSender) {
        this.mStateSender = stateSender;
        this.mChannel = channel;
    }

    @Override
    public boolean write() throws RuntimeException {
        if (!mChannel.isConnected()) {
            WriteException writeException = new WriteException("channel is shutdown");
            throw writeException;
        }
        ISendable sendable = null;
        if (!mQueue.isEmpty()) {
            sendable = mQueue.get(0);
            mQueue.remove(0);
        } else {
            return false;
        }

        if (sendable != null) {
            try {
                byte[] bytes = sendable.parse();
                int packageSize = mOkOptions.getSendSinglePackageBytes();
                int remainingCount = bytes.length;
                ByteBuffer writeBuf = ByteBuffer.allocate(packageSize);
                writeBuf.order(mOkOptions.getWriteOrder());
                int index = 0;
                while (remainingCount > 0) {
                    int realWriteLength = Math.min(packageSize, remainingCount);
                    writeBuf.clear();
                    writeBuf.rewind();
                    writeBuf.put(bytes, index, realWriteLength);
                    writeBuf.flip();
                    while (writeBuf.hasRemaining()) {
                        mChannel.write(writeBuf);
                    }
                    byte[] forLogBytes = Arrays.copyOfRange(bytes, index, index + realWriteLength);
                    SL.i("write bytes: " + BytesUtils.toHexStringForLog(forLogBytes));
                    SL.i("bytes write length:" + realWriteLength);

                    index += realWriteLength;
                    remainingCount -= realWriteLength;
                }
                if (sendable instanceof IPulseSendable) {
                    mStateSender.sendBroadcast(IAction.ACTION_PULSE_REQUEST, sendable);
                } else {
                    mStateSender.sendBroadcast(IAction.ACTION_WRITE_COMPLETE, sendable);
                }
            } catch (Exception e) {
                WriteException writeException = new WriteException(e);
                throw writeException;
            }
            return true;
        }
        return false;
    }

    @Override
    public void setOption(OkSocketOptions okOptions) {
        mOkOptions = okOptions;
    }

    @Override
    public void offer(ISendable sendable) {
        mQueue.add(sendable);
    }

    @Override
    public int queueSize() {
        return mQueue.size();
    }


}
