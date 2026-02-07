package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "CreateTimeSlotRequest",
        description = "Request payload for creating a new time slot",
        example = "{\"start\": \"2026-02-07T10:00:00Z\", \"duration_minutes\": 60}"
)
public class CreateTimeSlotRequestDto {

    @JsonProperty("start")
    @NotNull(message = "start time must not be null")
    @Schema(
            description = "The start date and time of the time slot (ISO 8601 format)",
            example = "2026-02-07T10:00:00Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Instant start;

    @JsonProperty("duration_minutes")
    @NotNull(message = "duration in minutes must not be null")
    @Positive(message = "duration in minutes must be positive")
    @Schema(
            description = "The duration of the time slot in minutes",
            example = "60",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    private Integer durationMinutes;
}
