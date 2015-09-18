package org.elevenframework.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InternalServerErrorException() {

    }

    public InternalServerErrorException(String message) {
        super(message);
    }
}
