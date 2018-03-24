package com.savi.organic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT)
public class DuplicateRecordException extends RuntimeException {
    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRecordException(String message) {
        super(message);
    }
}
