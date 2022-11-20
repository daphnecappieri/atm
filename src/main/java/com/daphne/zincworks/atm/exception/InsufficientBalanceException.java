package com.daphne.zincworks.atm.exception;


import org.springframework.http.HttpStatus;

/**
 * Returned when insufficient balance in the users account
 */
public class InsufficientBalanceException extends RestException {

    public InsufficientBalanceException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}