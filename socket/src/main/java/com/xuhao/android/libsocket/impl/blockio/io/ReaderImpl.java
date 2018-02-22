package com.xuhao.android.libsocket.impl.blockio.io;

import com.xuhao.android.libsocket.impl.exceptions.ReadException;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;
import com.xuhao.android.libsocket.sdk.connection.interfacies.IAction;
import com.xuhao.android.libsocket.sdk.protocol.IHeaderProtocol;
import com.xuhao.android.libsocket.utils.BytesUtils;
import com.xuhao.android.libsocket.utils.SL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by xuhao on 2017/5/31.
 */

public class ReaderImpl extends AbsReader {

    private ByteBuffer mRemainingBuf;

    public ReaderImpl(InputStream inputStream, IStateSender stateSender) {
        super(inputStream, stateSender);
    }

    @Override
    public void read() throws RuntimeException {
        OriginalData originalData = new OriginalData();
        IHeaderProtocol headerProtocol = mOkOptions.getHeaderProtocol();
        ByteBuffer headBuf = ByteBuffer.allocate(headerProtocol.getHeaderLength());
        headBuf.order(mOkOptions.getReadByteOrder());
        try {
            if (mRemainingBuf != null) {
                mRemainingBuf.flip();
                int length = Math.min(mRemainingBuf.remaining(), headerProtocol.getHeaderLength());
                headBuf.put(mRemainingBuf.array(), 0, length);
                if (length < headerProtocol.getHeaderLength()) {
                    //there are no data left
                    mRemainingBuf = null;
                    for (int i = 0; i < headerProtocol.getHeaderLength() - length; i++) {
                        headBuf.put((byte) mInputStream.read());
                    }
                } else {
                    mRemainingBuf.position(headerProtocol.getHeaderLength());
                }
            } else {
                for (int i = 0; i < headBuf.capacity(); i++) {
                    headBuf.put((byte) mInputStream.read());
                }
            }
            originalData.setHeadBytes(headBuf.array());
            if (OkSocketOptions.isDebug()) {
                SL.i("read head: " + BytesUtils.toHexStringForLog(headBuf.array()));
            }
            int bodyLength = headerProtocol.getBodyLength(originalData.getHeadBytes(), mOkOptions.getReadByteOrder());
            if (OkSocketOptions.isDebug()) {
                SL.i("need read body length: " + bodyLength);
            }
            if (bodyLength > 0) {
                if (bodyLength > mOkOptions.getMaxReadDataMB() * 1024 * 1024) {
                    throw new ReadException("we can't read data bigger than " + mOkOptions.getMaxReadDataMB() + "Mb");
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLength);
                byteBuffer.order(mOkOptions.getReadByteOrder());
                if (mRemainingBuf != null) {
                    int bodyStartPosition = mRemainingBuf.position();
                    int length = Math.min(mRemainingBuf.remaining(), bodyLength);
                    byteBuffer.put(mRemainingBuf.array(), bodyStartPosition, length);
                    mRemainingBuf.position(bodyStartPosition + length);
                    if (length == bodyLength) {
                        if (mRemainingBuf.remaining() > 0) {//there are data left
                            //TODO GitHub上代码bug,需要用临时对象存储数据后替换
                            ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                            temp.order(mOkOptions.getReadByteOrder());
                            temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                            mRemainingBuf = temp;
                        } else {//there are no data left
                            mRemainingBuf = null;
                        }
                        //cause this time data from remaining buffer not from channel.
                        originalData.setBodyBytes(byteBuffer.array());
                        mStateSender.sendBroadcast(IAction.ACTION_READ_COMPLETE, originalData);
                        return;
                    } else {//there are no data left in buffer and some data pieces in channel
                        mRemainingBuf = null;
                    }
                }
                readBodyFromChannel(byteBuffer);
                originalData.setBodyBytes(byteBuffer.array());
            } else if (bodyLength == 0) {
                //TODO 如果body长度为零，需要从缓存中剪掉已读取的头部数据
                if(null != mRemainingBuf){
                    if(mRemainingBuf.hasRemaining()){
                        ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                        temp.order(mOkOptions.getReadByteOrder());
                        temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                        mRemainingBuf = temp;
                    }else{
                        mRemainingBuf = null;
                    }
                }

                originalData.setBodyBytes(new byte[0]);
            } else if (bodyLength < 0) {
                throw new ReadException(
                        "this socket input stream has some problem,wrong body length " + bodyLength
                                + ",we'll disconnect");
            }
            mStateSender.sendBroadcast(IAction.ACTION_READ_COMPLETE, originalData);
        } catch (Exception e) {
            ReadException readException = new ReadException(e);
            throw readException;
        }
    }

    private void readBodyFromChannel(ByteBuffer byteBuffer) throws IOException {
        while (byteBuffer.hasRemaining()) {
            try {
                byte[] bufArray = new byte[mOkOptions.getReadSingleTimeBufferBytes()];
                int len = mInputStream.read(bufArray);
                if (len < 0) {
                    break;
                }
                int remaining = byteBuffer.remaining();
                if (len > remaining) {
                    byteBuffer.put(bufArray, 0, remaining);
                    mRemainingBuf = ByteBuffer.allocate(len - remaining);
                    mRemainingBuf.order(mOkOptions.getReadByteOrder());
                    mRemainingBuf.put(bufArray, remaining, len - remaining);
                } else {
                    byteBuffer.put(bufArray, 0, len);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        if (OkSocketOptions.isDebug()) {
            SL.i("read total bytes: " + BytesUtils.toHexStringForLog(byteBuffer.array()));
            SL.i("read total length:" + (byteBuffer.capacity() - byteBuffer.remaining()));
        }
    }

}
