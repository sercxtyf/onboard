package com.onboard.frontend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NoPermissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoPermissionException() {

    }

    public NoPermissionException(String message) {
        super(message);
    }
}
