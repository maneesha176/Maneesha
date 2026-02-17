package com.interviewslot.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ScheduleConflictException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("SCHEDULE_CONFLICT", e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InsufficientAvailabilityException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailability(InsufficientAvailabilityException e) {
        log.warn("No availability found: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NO_SLOTS_AVAILABLE", e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("RESOURCE_NOT_FOUND", e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred", LocalDateTime.now()));
    }
}
