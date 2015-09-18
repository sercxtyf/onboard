package com.onboard.frontend.exception.handler;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.google.common.base.Throwables;
import com.onboard.frontend.exception.BadRequestException;
import com.onboard.frontend.exception.NoLoginException;
import com.onboard.frontend.exception.NoPermissionException;
import com.onboard.frontend.exception.ResourceNotFoundException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger("global.logger");

    public class ErrorInfo {

        public final int code;

        public final String url;

        public final String ex;

        public ErrorInfo(int code, String url, Exception ex) {
            this.code = code;
            this.url = url;
            this.ex = Throwables.getStackTraceAsString(ex);
        }
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ErrorInfo> handleAccessDeniedException(HttpServletRequest request, Exception ex) {
        ErrorInfo e = new ErrorInfo(HttpStatus.FORBIDDEN.value(), request.getRequestURI(), ex);
        return new ResponseEntity<ErrorInfo>(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleResourceNotFoundException(HttpServletRequest request, Exception ex) {
        ErrorInfo e = new ErrorInfo(HttpStatus.NOT_FOUND.value(), request.getRequestURI(), ex);
        return new ResponseEntity<ErrorInfo>(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorInfo> handleResourceBadRequestException(HttpServletRequest request, Exception ex) {
        ErrorInfo e = new ErrorInfo(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), ex);
        return new ResponseEntity<ErrorInfo>(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoLoginException.class)
    public ResponseEntity<ErrorInfo> handleNoLoginException(HttpServletRequest request, Exception ex) {
        ErrorInfo e = new ErrorInfo(HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(), ex);
        return new ResponseEntity<ErrorInfo>(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleUncaughtedException(HttpServletRequest request, Exception ex) {
        MDC.put("url", request.getRequestURI());
        MDC.put("message", ex.getLocalizedMessage());
        logger.error(ex.getLocalizedMessage(), ex);
        MDC.clear();
        ErrorInfo e = new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), ex);
        return new ResponseEntity<ErrorInfo>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
