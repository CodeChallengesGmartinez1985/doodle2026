package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "TimeSlotFilters",
        description = "Filters for searching time slots"
)
public class SearchFiltersTimeSlotRequestDto {

    @JsonProperty("status")
    @Pattern(regexp = "^(AVAILABLE|BUSY)?$", message = "status must be either AVAILABLE or BUSY")
    @Schema(
            description = "Filter by time slot status (AVAILABLE or BUSY). Optional.",
            example = "AVAILABLE",
            allowableValues = {"AVAILABLE", "BUSY"}
    )
    private String status;

    @JsonProperty("start_time")
    @Schema(
            description = "Filter time slots starting from this time (ISO 8601 format). Optional.",
            example = "2026-02-08T00:00:00Z"
    )
    private Instant startTime;

    @JsonProperty("end_time")
    @Schema(
            description = "Filter time slots ending before this time (ISO 8601 format). Optional.",
            example = "2026-02-15T23:59:59Z"
    )
    private Instant endTime;
}
