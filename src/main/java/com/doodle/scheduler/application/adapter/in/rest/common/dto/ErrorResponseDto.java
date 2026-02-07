package com.doodle.scheduler.application.adapter.in.rest.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ErrorResponse",
        description = "Standard error response payload",
        example = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Invalid request\", \"path\": \"/api/v1/timeslots\"}"
)
public class ErrorResponseDto {

    @JsonProperty("timestamp")
    @Schema(
            description = "The timestamp when the error occurred (ISO 8601 format)",
            example = "2026-02-07T09:15:00Z"
    )
    private Instant timestamp;

    @JsonProperty("status")
    @Schema(
            description = "HTTP status code",
            example = "400"
    )
    private int status;

    @JsonProperty("error")
    @Schema(
            description = "HTTP status reason phrase",
            example = "Bad Request"
    )
    private String error;

    @JsonProperty("message")
    @Schema(
            description = "Detailed error message",
            example = "Invalid request data"
    )
    private String message;

    @JsonProperty("path")
    @Schema(
            description = "The request path that caused the error",
            example = "/api/v1/timeslots"
    )
    private String path;
}
