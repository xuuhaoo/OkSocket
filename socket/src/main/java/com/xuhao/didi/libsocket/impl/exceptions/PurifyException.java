package com.xuhao.didi.libsocket.impl.exceptions;

/**
 * Created by xuhao on 2017/5/22.
 */

public class PurifyException extends RuntimeException {
    public PurifyException() {
        super();
    }

    public PurifyException(String message) {
        super(message);
    }

    public PurifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PurifyException(Throwable cause) {
        super(cause);
    }

    protected PurifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
