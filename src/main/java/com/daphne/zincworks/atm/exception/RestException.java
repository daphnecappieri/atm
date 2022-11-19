package com.daphne.zincworks.atm.exception;


import org.springframework.http.HttpStatus;

/**
 * Abstract RuntimeException which can be thrown to cause a provided status code to be returned.
 */
public abstract class RestException extends RuntimeException {
    private final HttpStatus status;

    public RestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}