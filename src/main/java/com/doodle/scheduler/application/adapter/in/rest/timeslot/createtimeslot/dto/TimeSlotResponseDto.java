package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "TimeSlotResponse",
        description = "Response payload containing time slot details",
        example = "{\"id\": \"550e8400-e29b-41d4-a716-446655440000\", \"start\": \"2026-02-07T10:00:00Z\", \"end\": \"2026-02-07T11:00:00Z\", \"duration_minutes\": 60, \"state\": \"AVAILABLE\"}"
)
public class TimeSlotResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the time slot",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private UUID id;


    @JsonProperty("start")
    @Schema(
            description = "The start date and time of the time slot (ISO 8601 format)",
            example = "2026-02-07T10:00:00Z"
    )
    private Instant start;

    @JsonProperty("end")
    @Schema(
            description = "The end date and time of the time slot (ISO 8601 format)",
            example = "2026-02-07T11:00:00Z"
    )
    private Instant end;

    @JsonProperty("duration_minutes")
    @Schema(
            description = "The duration of the time slot in minutes",
            example = "60"
    )
    private Integer durationMinutes;

    @JsonProperty("state")
    @Schema(
            description = "Current state of the time slot (e.g., AVAILABLE, BOOKED)",
            example = "AVAILABLE"
    )
    private String state;
}
