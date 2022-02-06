package com.github.maciejmalewicz.Desert21.controller.advice;

import com.github.maciejmalewicz.Desert21.exceptions.NotAcceptableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class MainControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(
            {
                    NotAcceptableException.class
            })
    public ResponseEntity<?> handleNotAcceptableException(Exception ex, WebRequest request){
        return handleExceptionInternal(ex, ex.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }
}
