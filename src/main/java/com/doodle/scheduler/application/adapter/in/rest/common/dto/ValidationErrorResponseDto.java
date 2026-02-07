package com.doodle.scheduler.application.adapter.in.rest.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(
        name = "ValidationErrorResponse",
        description = "Error response payload with field-level validation details",
        example = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Validation failed\", \"path\": \"/api/v1/timeslots\", \"fieldErrors\": [{\"field\": \"start\", \"message\": \"start time must not be null\"}]}"
)
public class ValidationErrorResponseDto extends ErrorResponseDto {

    @JsonProperty("fieldErrors")
    @Schema(
            description = "List of field-level validation errors",
            example = "[{\"field\": \"start\", \"message\": \"start time must not be null\"}]"
    )
    private List<FieldError> fieldErrors = new ArrayList<>();

    public ValidationErrorResponseDto(Instant timestamp, int status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
        this.fieldErrors = new ArrayList<>();
    }

    public void addFieldError(String field, String message) {
        fieldErrors.add(new FieldError(field, message));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(name = "FieldError", description = "Field-level validation error details")
    public static class FieldError {
        @JsonProperty("field")
        @Schema(description = "The field name that failed validation", example = "start")
        private String field;

        @JsonProperty("message")
        @Schema(description = "The validation error message", example = "start time must not be null")
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
