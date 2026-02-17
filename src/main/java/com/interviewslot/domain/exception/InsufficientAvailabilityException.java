package com.interviewslot.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InsufficientAvailabilityException extends RuntimeException {
    public InsufficientAvailabilityException(String message) {
        super(message);
    }
}
