package com.doodle.scheduler.application.adapter.in.rest.timeslot.deletetimeslot;

import com.doodle.scheduler.application.adapter.in.rest.common.ControllerConstants;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.common.BaseTimeSlotController;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DeleteTimeSlotController extends BaseTimeSlotController {

    private final DeleteTimeSlotUseCase deleteTimeSlotUseCase;

    public DeleteTimeSlotController(DeleteTimeSlotUseCase deleteTimeSlotUseCase) {
        this.deleteTimeSlotUseCase = deleteTimeSlotUseCase;
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a time slot",
            description = "Deletes an existing time slot by its ID. The time slot must belong to the authenticated user and must not be assigned to any meeting."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Time slot successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Invalid UUID format",
                                    description = "The provided ID is not a valid UUID",
                                    value = "{\"timestamp\": \"2026-02-08T10:15:00Z\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Invalid UUID format for parameter 'id'\", \"path\": \"/api/v1/timeslots/invalid-uuid\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Time slot not found or does not belong to the user",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Time slot not found",
                                    description = "The time slot does not exist or does not belong to the authenticated user",
                                    value = "{\"timestamp\": \"2026-02-08T10:15:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Time slot not found with id: 550e8400-e29b-41d4-a716-446655440000\", \"path\": \"/api/v1/timeslots/550e8400-e29b-41d4-a716-446655440000\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Time slot is assigned to a meeting and cannot be deleted",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Slot assigned to meeting",
                                    description = "The time slot cannot be deleted because it is assigned to a meeting",
                                    value = "{\"timestamp\": \"2026-02-08T10:15:00Z\", \"status\": 409, \"error\": \"Conflict\", \"message\": \"time slot is used by a meeting and cannot be deleted\", \"path\": \"/api/v1/timeslots/550e8400-e29b-41d4-a716-446655440000\"}"
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
                                    value = "{\"timestamp\": \"2026-02-08T10:15:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"An unexpected error occurred\", \"path\": \"/api/v1/timeslots/550e8400-e29b-41d4-a716-446655440000\"}"
                            )
                    )
            )
    })
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable UUID id) {
        var command = new DeleteTimeSlotCommand(ControllerConstants.USERNAME, id);
        deleteTimeSlotUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }
}
