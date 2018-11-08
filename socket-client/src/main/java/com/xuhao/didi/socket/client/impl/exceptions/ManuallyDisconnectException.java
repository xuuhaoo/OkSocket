package com.xuhao.didi.socket.client.impl.exceptions;

/**
 * Created by didi on 2018/6/4.
 */

public class ManuallyDisconnectException extends RuntimeException {

    public ManuallyDisconnectException() {
        super();
    }

    public ManuallyDisconnectException(String message) {
        super(message);
    }

    public ManuallyDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManuallyDisconnectException(Throwable cause) {
        super(cause);
    }
}
