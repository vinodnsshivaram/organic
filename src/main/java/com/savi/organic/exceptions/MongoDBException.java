package com.savi.organic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
public class MongoDBException extends RuntimeException {
    public MongoDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoDBException(String message) {
        super(message);
    }
}