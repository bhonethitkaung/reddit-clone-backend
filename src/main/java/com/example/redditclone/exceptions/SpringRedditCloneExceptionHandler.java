package com.example.redditclone.exceptions;

import com.example.redditclone.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SpringRedditCloneExceptionHandler {


    @ExceptionHandler(SpringRedditCloneException.class)
    public ResponseEntity<ErrorResponse> onSpringRedditCloneException(SpringRedditCloneException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage()));
    }
}
