package com.ckontur.pkr.common.exception;

public class UpdateEntityException extends RuntimeException {
    public UpdateEntityException(String message) {
        super(message);
    }

    public UpdateEntityException(String message, Throwable t) {
        super(message, t);
    }
}
