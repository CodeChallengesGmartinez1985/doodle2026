package com.doodle.scheduler.application.adapter.in.rest.timeslot.deletetimeslot;

import com.doodle.scheduler.application.adapter.in.rest.BaseRestTest;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.exception.SlotAssignedToMeetingException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotFoundException;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {DeleteTimeSlotController.class, DeleteTimeSlotControllerAdvice.class})
@DisplayName("DeleteTimeSlotController - Slice Test")
class DeleteTimeSlotControllerSliceTest extends BaseRestTest {

    @MockitoBean
    private DeleteTimeSlotUseCase deleteTimeSlotUseCase;

    private static final String BASE_URL = "/api/v1/timeslots";

    @Nested
    @DisplayName("GIVEN valid time slot ID")
    class SuccessScenarios {

        @Test
        @DisplayName("WHEN deleting time slot THEN should return 204 NO CONTENT")
        void shouldDeleteTimeSlotSuccessfully() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();

            // WHEN
            doNothing().when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist());

            verify(deleteTimeSlotUseCase, times(1)).execute(any(DeleteTimeSlotCommand.class));
        }
    }

    @Nested
    @DisplayName("GIVEN invalid time slot ID format")
    class InvalidUuidScenarios {

        @Test
        @DisplayName("WHEN ID is not a valid UUID THEN should return 400 BAD REQUEST")
        void shouldRejectInvalidUuidFormat() throws Exception {
            // GIVEN
            String invalidId = "not-a-valid-uuid";

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + invalidId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value(containsString("Invalid UUID format")))
                    .andExpect(jsonPath("$.path").value(containsString("/api/v1/timeslots/")));

            verify(deleteTimeSlotUseCase, never()).execute(any());
        }
    }

    @Nested
    @DisplayName("GIVEN time slot not found or not owned by user")
    class NotFoundScenarios {

        @Test
        @DisplayName("WHEN time slot does not exist THEN should return 404 NOT FOUND")
        void shouldReturn404WhenTimeSlotNotFound() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();
            String errorMessage = "Time slot not found with id: " + timeSlotId;

            doThrow(new TimeSlotNotFoundException(errorMessage))
                    .when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value(errorMessage))
                    .andExpect(jsonPath("$.path").value("/api/v1/timeslots/" + timeSlotId));
        }

        @Test
        @DisplayName("WHEN time slot does not belong to user THEN should return 404 NOT FOUND")
        void shouldReturn404WhenTimeSlotDoesNotBelongToUser() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();
            String errorMessage = "Time slot not found with id: " + timeSlotId;

            doThrow(new TimeSlotNotFoundException(errorMessage))
                    .when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }
    }

    @Nested
    @DisplayName("GIVEN time slot assigned to meeting")
    class ConflictScenarios {

        @Test
        @DisplayName("WHEN time slot is assigned to meeting THEN should return 409 CONFLICT")
        void shouldReturn409WhenTimeSlotAssignedToMeeting() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();
            String errorMessage = "time slot is used by a meeting and cannot be deleted";

            doThrow(new SlotAssignedToMeetingException(errorMessage))
                    .when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value(errorMessage))
                    .andExpect(jsonPath("$.path").value("/api/v1/timeslots/" + timeSlotId));
        }
    }

    @Nested
    @DisplayName("GIVEN database errors")
    class DatabaseErrorScenarios {

        @Test
        @DisplayName("WHEN database error occurs THEN should return 500 INTERNAL SERVER ERROR")
        void shouldReturn500OnDatabaseError() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();

            doThrow(new DataAccessException("Database connection failed") {})
                    .when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }

        @Test
        @DisplayName("WHEN unexpected error occurs THEN should return 500 INTERNAL SERVER ERROR")
        void shouldReturn500OnUnexpectedError() throws Exception {
            // GIVEN
            UUID timeSlotId = UUID.randomUUID();

            doThrow(new RuntimeException("Unexpected error"))
                    .when(deleteTimeSlotUseCase).execute(any(DeleteTimeSlotCommand.class));

            // WHEN & THEN
            mockMvc.perform(delete(BASE_URL + "/" + timeSlotId))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }
    }
}
