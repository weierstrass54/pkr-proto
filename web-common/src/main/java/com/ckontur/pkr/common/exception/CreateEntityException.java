package com.ckontur.pkr.common.exception;

public class CreateEntityException extends RuntimeException {
    public CreateEntityException(String message) {
        super(message);
    }

    public CreateEntityException(String message, Throwable t) {
        super(message, t);
    }
}
