package com.onboard.frontend.exception;

public class NoLoginException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NoLoginException() {

    }

    public NoLoginException(RuntimeException runtimeException) {
        super(runtimeException);
    }

    public NoLoginException(String message) {
        super(message);
    }

}
