package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots;

import com.doodle.scheduler.application.adapter.in.rest.common.ControllerConstants;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.common.BaseTimeSlotController;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsResponseDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.mapper.SearchTimeSlotsDtoMapper;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchTimeSlotsController extends BaseTimeSlotController {

    private final SearchTimeSlotsUseCase searchTimeSlotsUseCase;
    private final SearchTimeSlotsDtoMapper searchTimeSlotsDtoMapper;

    public SearchTimeSlotsController(SearchTimeSlotsUseCase searchTimeSlotsUseCase,
                                     SearchTimeSlotsDtoMapper searchTimeSlotsDtoMapper) {
        this.searchTimeSlotsUseCase = searchTimeSlotsUseCase;
        this.searchTimeSlotsDtoMapper = searchTimeSlotsDtoMapper;
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search time slots with filters and pagination",
            description = "Search for time slots belonging to the authenticated user. " +
                         "Supports optional filtering by status (AVAILABLE/BUSY) and time frame (start/end time). " +
                         "Results are paginated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Time slots retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchTimeSlotsResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Success response",
                                    description = "Search results with pagination",
                                    value = """
                                            {
                                              "time_slots": [
                                                {
                                                  "id": "550e8400-e29b-41d4-a716-446655440000",
                                                  "start": "2026-02-08T10:00:00Z",
                                                  "end": "2026-02-08T11:00:00Z",
                                                  "duration_minutes": 60,
                                                  "state": "AVAILABLE"
                                                }
                                              ],
                                              "total_elements": 42,
                                              "total_pages": 5,
                                              "current_page": 0,
                                              "page_size": 10
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid status",
                                    description = "Invalid status value provided",
                                    value = "{\"timestamp\": \"2026-02-08T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"status must be either AVAILABLE or BUSY\", \"path\": \"/api/v1/timeslots/search\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "User not found",
                                    description = "The authenticated user was not found in the system",
                                    value = "{\"timestamp\": \"2026-02-08T09:15:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User not found with username: testuser\", \"path\": \"/api/v1/timeslots/search\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Server error",
                                    description = "An unexpected error occurred on the server",
                                    value = "{\"timestamp\": \"2026-02-08T09:15:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"An unexpected error occurred\", \"path\": \"/api/v1/timeslots/search\"}"
                            )
                    )
            )
    })
    public ResponseEntity<SearchTimeSlotsResponseDto> searchTimeSlots(
            @Valid @RequestBody SearchTimeSlotsRequestDto requestDto) {

        var command = new SearchTimeSlotsCommand(
                ControllerConstants.USERNAME,
                requestDto.getStatus(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getPage(),
                requestDto.getSize()
        );

        var result = searchTimeSlotsUseCase.execute(command);
        var responseDto = searchTimeSlotsDtoMapper.toSearchResponseDto(result);

        return ResponseEntity.ok(responseDto);
    }
}
