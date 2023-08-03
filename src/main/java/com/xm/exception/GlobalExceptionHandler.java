package com.xm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnsupportedCryptoException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedCryptoException(UnsupportedCryptoException ex) {
        return new ErrorResponse("UNSUPPORTED_CRYPTO", ex.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(DataNotFoundException ex) {
        return new ErrorResponse("DATA_NOT_FOUND", ex.getMessage());
    }
}