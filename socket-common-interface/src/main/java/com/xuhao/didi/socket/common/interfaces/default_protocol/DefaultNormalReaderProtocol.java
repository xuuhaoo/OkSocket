package com.xuhao.didi.socket.common.interfaces.default_protocol;



import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DefaultNormalReaderProtocol implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return 4;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (header == null || header.length < getHeaderLength()) {
            return 0;
        }
        ByteBuffer bb = ByteBuffer.wrap(header);
        bb.order(byteOrder);
        return bb.getInt();
    }
}