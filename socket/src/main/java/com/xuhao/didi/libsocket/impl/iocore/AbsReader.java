package com.xuhao.didi.libsocket.impl.iocore;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;

import com.xuhao.didi.common.interfacies.IIOCoreOptions;
import com.xuhao.didi.common.interfacies.client.io.IReader;
import com.xuhao.didi.common.interfacies.dispatcher.IStateSender;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tony on 2017/12/26.
 */

public abstract class AbsReader implements IReader<IIOCoreOptions> {

    protected IIOCoreOptions mOkOptions;

    protected IStateSender mStateSender;

    protected InputStream mInputStream;

    public AbsReader() {
    }

    @CallSuper
    @Override
    public void initialize(InputStream inputStream, IStateSender stateSender) {
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
