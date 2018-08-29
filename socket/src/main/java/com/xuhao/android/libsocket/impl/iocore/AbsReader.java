package com.xuhao.android.libsocket.impl.iocore;

import android.support.annotation.MainThread;

import com.xuhao.android.common.interfacies.IIOCoreOptions;
import com.xuhao.android.common.interfacies.client.io.IReader;
import com.xuhao.android.common.interfacies.dispatcher.IStateSender;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tony on 2017/12/26.
 */

public abstract class AbsReader implements IReader<IIOCoreOptions> {

    protected IIOCoreOptions mOkOptions;

    protected IStateSender mStateSender;

    protected InputStream mInputStream;

    public AbsReader(InputStream inputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mInputStream = inputStream;
    }

    @Override
    @MainThread
    public void setOption(IIOCoreOptions option) {
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
