package com.doodle.scheduler.application.adapter.in.rest.timeslot.deletetimeslot;

import com.doodle.scheduler.application.adapter.in.rest.common.dto.ErrorResponseDto;
import com.doodle.scheduler.application.domain.calendar.exception.SlotAssignedToMeetingException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Exception handler for DeleteTimeSlotController.
 * Handles all exceptions that can occur during timeslot deletion and provides
 * standardized error responses with appropriate HTTP status codes and logging.
 */
@RestControllerAdvice(assignableTypes = DeleteTimeSlotController.class)
@Slf4j
public class DeleteTimeSlotControllerAdvice {

    /**
     * Handles TimeSlotNotFoundException (time slot not found or not owned by user).
     * Returns 404 Not Found.
     */
    @ExceptionHandler(TimeSlotNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTimeSlotNotFoundException(
            TimeSlotNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Time slot not found for request to {}: {}", request.getRequestURI(), ex.getMessage());

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
     * Handles SlotAssignedToMeetingException (time slot assigned to a meeting).
     * Returns 409 Conflict.
     */
    @ExceptionHandler(SlotAssignedToMeetingException.class)
    public ResponseEntity<ErrorResponseDto> handleSlotAssignedToMeetingException(
            SlotAssignedToMeetingException ex,
            HttpServletRequest request) {

        log.warn("Cannot delete time slot for request to {}: {}", request.getRequestURI(), ex.getMessage());

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
     * Handles MethodArgumentTypeMismatchException (invalid UUID format).
     * Returns 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Invalid argument type for request to {}: {}", request.getRequestURI(), ex.getMessage());

        String message = String.format("Invalid UUID format for parameter '%s'", ex.getName());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles generic DataAccessException (database errors).
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccessException(
            DataAccessException ex,
            HttpServletRequest request) {

        log.error("Database error during time slot deletion for request to {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

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
     * Handles generic exceptions.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error during time slot deletion for request to {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

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
