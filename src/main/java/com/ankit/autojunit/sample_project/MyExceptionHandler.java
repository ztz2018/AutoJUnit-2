package com.ankit.autojunit.sample_project;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity handleConflict(RuntimeException ex, WebRequest request) {

        return ResponseEntity.ok("It's ok! The exception "
                + " has been caught and is being diagnosed. ex msg : "
                + ex.getMessage());

    }



}
