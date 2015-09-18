package com.onboard.frontend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadRequestException() {

    }

    public BadRequestException(RuntimeException runtimeException) {
        super(runtimeException);
    }

    public BadRequestException(String message) {
        super(message);
    }
}
