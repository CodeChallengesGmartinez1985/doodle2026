package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot;

import com.doodle.scheduler.application.adapter.in.rest.common.dto.ErrorResponseDto;
import com.doodle.scheduler.application.adapter.in.rest.common.dto.ValidationErrorResponseDto;
import com.doodle.scheduler.application.domain.calendar.exception.InvalidTimeRangeException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeRangeInvalidDurationException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotCollisionException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotInvalidIdException;
import com.doodle.scheduler.application.domain.common.exception.DomainException;
import com.doodle.scheduler.application.domain.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Exception handler for CreateTimeSlotController.
 * Handles all exceptions that can occur during timeslot creation and provides
 * standardized error responses with appropriate HTTP status codes and logging.
 */
@RestControllerAdvice(assignableTypes = CreateTimeSlotController.class)
@Slf4j
public class CreateTimeSlotControllerAdvice {

    /**
     * Handles validation errors from @Valid annotation on request body.
     * Returns 400 Bad Request with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Validation failed for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI()
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles constraint violation exceptions from @Validated annotation.
     * Returns 400 Bad Request with field-level error details.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        log.warn("Constraint violation for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ValidationErrorResponseDto errorResponse = new ValidationErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI()
        );

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            errorResponse.addFieldError(fieldName, violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles invalid duration exceptions from TimeRange validation.
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(TimeRangeInvalidDurationException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeRangeInvalidDuration(
            TimeRangeInvalidDurationException ex,
            HttpServletRequest request) {

        log.warn("Invalid duration for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles invalid time range exceptions (e.g., end before start).
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(InvalidTimeRangeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTimeRange(
            InvalidTimeRangeException ex,
            HttpServletRequest request) {

        log.warn("Invalid time range for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles user not found exceptions.
     * Returns 404 Not Found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        log.warn("User not found for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles timeslot collision exceptions (overlapping timeslots).
     * Returns 409 Conflict.
     */
    @ExceptionHandler(TimeSlotCollisionException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeSlotCollision(
            TimeSlotCollisionException ex,
            HttpServletRequest request) {

        log.warn("Timeslot collision for request to {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles timeslot invalid ID exceptions (ID collision - very rare).
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(TimeSlotInvalidIdException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeSlotInvalidId(
            TimeSlotInvalidIdException ex,
            HttpServletRequest request) {

        log.error("Timeslot ID collision for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An internal error occurred while creating the timeslot",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles data integrity violations from the database layer.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Data integrity violation for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "A database constraint was violated",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles generic data access exceptions from the database layer.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(
            DataAccessException ex,
            HttpServletRequest request) {

        log.error("Data access error for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An error occurred while accessing the database",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles null pointer exceptions (should rarely occur with proper validation).
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponseDto> handleNullPointerException(
            NullPointerException ex,
            HttpServletRequest request) {

        log.error("Null pointer exception for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles any other domain exceptions not explicitly caught above.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDto> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {

        log.error("Domain exception for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles any unexpected exceptions as a last resort.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected exception for request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
