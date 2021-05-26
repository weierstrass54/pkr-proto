package com.ckontur.pkr.common.exception;

public class InvalidArgumentException extends RuntimeException {

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable t) {
        super(message, t);
    }

}
