package com.xuhao.didi.socket.client.impl.exceptions;

/**
 * Created by xuhao on 2017/6/5.
 */

public class DogDeadException extends RuntimeException {
    public DogDeadException() {
        super();
    }

    public DogDeadException(String message) {
        super(message);
    }

    public DogDeadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DogDeadException(Throwable cause) {
        super(cause);
    }

    protected DogDeadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
