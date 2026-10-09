package com.smartpool.backend.controller;

import com.smartpool.backend.service.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Turns exceptions into small JSON errors. */
@RestControllerAdvice
public class ApiExceptionHandler {
    public record ErrorBody(int status, String message) {}

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorBody notFound(NotFoundException e) {
        return new ErrorBody(404, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorBody conflict(IllegalStateException e) {
        return new ErrorBody(409, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorBody invalid(MethodArgumentNotValidException e) {
        var error = e.getBindingResult().getFieldErrors().get(0);
        return new ErrorBody(400, error.getField() + " " + error.getDefaultMessage());
    }
}
