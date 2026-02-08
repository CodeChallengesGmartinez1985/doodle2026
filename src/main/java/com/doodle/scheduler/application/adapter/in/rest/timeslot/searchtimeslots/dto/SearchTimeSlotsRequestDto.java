package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto;

import com.doodle.scheduler.application.adapter.in.rest.common.dto.PaginationDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SearchTimeSlotsRequest",
        description = "Request payload for searching time slots with optional filters and pagination",
        example = "{\"filters\": {\"status\": \"AVAILABLE\", \"start_time\": \"2026-02-08T00:00:00Z\", \"end_time\": \"2026-02-15T23:59:59Z\"}, \"pagination\": {\"page\": 0, \"size\": 10}}"
)
public class SearchTimeSlotsRequestDto {

    @JsonProperty("filters")
    @Valid
    @Schema(
            description = "Filtering criteria for time slots. All fields are optional.",
            implementation = SearchFiltersTimeSlotRequestDto.class
    )
    private SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto();

    @JsonProperty("pagination")
    @Valid
    @NotNull(message = "pagination cannot be null")
    @Schema(
            description = "Pagination parameters",
            implementation = PaginationDto.class
    )
    private PaginationDto pagination = new PaginationDto();

    // Convenience methods for backward compatibility or easier access
    public String getStatus() {
        return filters != null ? filters.getStatus() : null;
    }

    public Instant getStartTime() {
        return filters != null ? filters.getStartTime() : null;
    }

    public Instant getEndTime() {
        return filters != null ? filters.getEndTime() : null;
    }

    public Integer getPage() {
        return pagination != null ? pagination.getPage() : 0;
    }

    public Integer getSize() {
        return pagination != null ? pagination.getSize() : 10;
    }
}
