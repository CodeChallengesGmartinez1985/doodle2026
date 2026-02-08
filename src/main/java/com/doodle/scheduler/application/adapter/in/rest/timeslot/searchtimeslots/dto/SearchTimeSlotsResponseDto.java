package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto;

import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SearchTimeSlotsResponse",
        description = "Response payload containing search results with pagination information"
)
public class SearchTimeSlotsResponseDto {

    @JsonProperty("time_slots")
    @Schema(description = "List of time slots matching the search criteria")
    private List<TimeSlotResponseDto> timeSlots;

    @JsonProperty("total_elements")
    @Schema(description = "Total number of elements matching the criteria", example = "42")
    private long totalElements;

    @JsonProperty("total_pages")
    @Schema(description = "Total number of pages", example = "5")
    private int totalPages;

    @JsonProperty("current_page")
    @Schema(description = "Current page number (zero-based)", example = "0")
    private int currentPage;

    @JsonProperty("page_size")
    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;
}
