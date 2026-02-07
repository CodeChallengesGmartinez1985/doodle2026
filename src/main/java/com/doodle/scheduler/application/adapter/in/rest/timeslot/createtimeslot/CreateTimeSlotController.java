package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot;

import com.doodle.scheduler.application.adapter.in.rest.common.ControllerConstants;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.common.BaseTimeSlotController;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.CreateTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.mapper.TimeSlotDtoMapper;
import com.doodle.scheduler.application.domain.commands.calendar.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.port.in.CreateTimeSlotUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class CreateTimeSlotController extends BaseTimeSlotController {

    private final CreateTimeSlotUseCase createTimeSlotUseCase;
    private final TimeSlotDtoMapper timeSlotDtoMapper;

    public CreateTimeSlotController(CreateTimeSlotUseCase createTimeSlotUseCase, TimeSlotDtoMapper timeSlotDtoMapper) {
        this.createTimeSlotUseCase = createTimeSlotUseCase;
        this.timeSlotDtoMapper = timeSlotDtoMapper;
    }

    @PostMapping
    @Operation(
            summary = "Create a new time slot",
            description = "Creates a new available time slot for the authenticated user. The time slot is defined by a start time and duration in minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Time slot successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TimeSlotResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Success response",
                                    description = "Time slot created successfully",
                                    value = "{\"id\": \"550e8400-e29b-41d4-a716-446655440000\", \"start\": \"2026-02-07T10:00:00Z\", \"end\": \"2026-02-07T11:00:00Z\", \"duration_minutes\": 60, \"state\": \"AVAILABLE\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body - missing or invalid fields",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Missing start time",
                                            description = "Request body is missing the required 'start' field",
                                            value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"start time must not be null\", \"path\": \"/api/v1/timeslots\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid duration",
                                            description = "Duration must be a positive number",
                                            value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"duration in minutes must be positive\", \"path\": \"/api/v1/timeslots\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Missing duration_minutes",
                                            description = "Request body is missing the required 'duration_minutes' field",
                                            value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"duration in minutes must not be null\", \"path\": \"/api/v1/timeslots\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid time range",
                                            description = "Time range validation failed",
                                            value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"durationMinutes must be > 0\", \"path\": \"/api/v1/timeslots\"}"
                                    )
                            }
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
                                    value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"User not found with username: testuser\", \"path\": \"/api/v1/timeslots\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Timeslot collision - overlaps with existing timeslot",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Timeslot collision",
                                    description = "The new timeslot overlaps with an existing timeslot",
                                    value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 409, \"error\": \"Conflict\", \"message\": \"time slot overlaps an existing slot: 550e8400-e29b-41d4-a716-446655440000\", \"path\": \"/api/v1/timeslots\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Server error",
                                            description = "An unexpected error occurred on the server",
                                            value = "{\"timestamp\": \"2026-02-07T09:15:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"An unexpected error occurred\", \"path\": \"/api/v1/timeslots\"}"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<TimeSlotResponseDto> createTimeSlot(
            @Valid @RequestBody CreateTimeSlotRequestDto requestDto) {
        var command = new CreateTimeSlotCommand(
                ControllerConstants.USERNAME,
                requestDto.getStart(),
                requestDto.getDurationMinutes()
        );
        var timeSlot = createTimeSlotUseCase.execute(command);
        var responseDto = timeSlotDtoMapper.toResponseDto(timeSlot);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
