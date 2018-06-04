package com.xuhao.android.libsocket.impl.blockio.io;

import android.support.annotation.MainThread;

import com.xuhao.android.libsocket.impl.abilities.IReader;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.connection.abilities.IStateSender;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tony on 2017/12/26.
 */

public abstract class AbsReader implements IReader {

    protected OkSocketOptions mOkOptions;

    protected IStateSender mStateSender;

    protected InputStream mInputStream;

    public AbsReader(InputStream inputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mInputStream = inputStream;
    }

    @Override
    @MainThread
    public void setOption(OkSocketOptions option) {
        mOkOptions = option;
    }


    @Override
    public void close() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }
}
