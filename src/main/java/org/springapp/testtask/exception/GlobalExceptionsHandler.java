package org.springapp.testtask.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(BirthdateRangeException.class)
    public ResponseEntity<String> handleBadBirthdateException(BirthdateRangeException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex.getMessage());
    }

    @ExceptionHandler(NoUserException.class)
    public ResponseEntity<String> handleUserNotFound(NoUserException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex.getMessage());
    }

    @ExceptionHandler(UserCreatedException.class)
    public ResponseEntity<String> handleUserExists(UserCreatedException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errorMessage.append(violation.getMessage());
            errorMessage.append("!");
        });
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorMessage.toString());
    }

    @ExceptionHandler(UserYearsException.class)
    public ResponseEntity<String> handleUserExists(UserYearsException ex) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).body(ex.getMessage());
    }
}
