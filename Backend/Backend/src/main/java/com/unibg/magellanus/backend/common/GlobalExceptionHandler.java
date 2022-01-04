package com.unibg.magellanus.backend.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler 
extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value 
    = { IllegalArgumentException.class, IllegalStateException.class })
  protected ResponseEntity<Object> handleConflict(
    RuntimeException ex, WebRequest request) {
      return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }
}