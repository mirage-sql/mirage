package com.miragesql.miragesql.exception;

public class TwoWaySQLException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TwoWaySQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwoWaySQLException(String message) {
        super(message);
    }

    public TwoWaySQLException(Throwable cause) {
        super(cause);
    }

}
