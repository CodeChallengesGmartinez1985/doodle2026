package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot;

import com.doodle.scheduler.application.adapter.in.rest.BaseRestTest;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.CreateTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.mapper.TimeSlotDtoMapperImpl;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.objectmother.TimeSlotMother;
import com.doodle.scheduler.application.domain.calendar.exception.InvalidTimeRangeException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeRangeInvalidDurationException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotCollisionException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotInvalidIdException;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.command.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.CreateTimeSlotUseCase;
import com.doodle.scheduler.application.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CreateTimeSlotController.class, CreateTimeSlotControllerAdvice.class})
@Import(TimeSlotDtoMapperImpl.class)
@DisplayName("CreateTimeSlotController - Slice Test")
class CreateTimeSlotControllerSliceTest extends BaseRestTest {

    @MockitoBean
    private CreateTimeSlotUseCase createTimeSlotUseCase;

    private static final String BASE_URL = "/api/v1/timeslots";

    @Nested
    @DisplayName("GIVEN valid request data")
    class SuccessScenarios {

        @Test
        @DisplayName("WHEN creating time slot THEN should return 201 CREATED with time slot details")
        void shouldCreateTimeSlotSuccessfully() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            TimeSlot timeSlot = TimeSlotMother.createValidTimeSlot();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class))).thenReturn(timeSlot);
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(TimeSlotMother.defaultId().toString()))
                    .andExpect(jsonPath("$.start").value("2026-02-07T10:00:00Z"))
                    .andExpect(jsonPath("$.end").value("2026-02-07T11:00:00Z"))
                    .andExpect(jsonPath("$.duration_minutes").value(60))
                    .andExpect(jsonPath("$.state").value("AVAILABLE"));
        }

        @Test
        @DisplayName("WHEN creating time slot with different duration THEN should return 201 with correct end time")
        void shouldCreateTimeSlotWithCustomDuration() throws Exception {
            // GIVEN
            Instant start = Instant.parse("2026-02-07T14:00:00Z");
            int duration = 90;
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createRequestWith(start, duration);
            TimeSlot timeSlot = TimeSlotMother.createTimeSlotWith(UUID.randomUUID(), start, duration);
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class))).thenReturn(timeSlot);
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.duration_minutes").value(90))
                    .andExpect(jsonPath("$.start").value("2026-02-07T14:00:00Z"))
                    .andExpect(jsonPath("$.end").value("2026-02-07T15:30:00Z"));
        }
    }

    @Nested
    @DisplayName("GIVEN invalid request validation")
    class ValidationErrorScenarios {

        @Test
        @DisplayName("WHEN start time is null THEN should return 400 BAD REQUEST with validation error")
        void shouldRejectNullStartTime() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createRequestWithNullStart();
            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.path").value(BASE_URL))
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'start')]").exists());
        }

        @Test
        @DisplayName("WHEN duration is null THEN should return 400 BAD REQUEST with validation error")
        void shouldRejectNullDuration() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createRequestWithNullDuration();
            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'durationMinutes')]").exists());
        }

        @Test
        @DisplayName("WHEN duration is negative THEN should return 400 BAD REQUEST with validation error")
        void shouldRejectNegativeDuration() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createRequestWithNegativeDuration();
            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.fieldErrors[?(@.field == 'durationMinutes' && @.message =~ /.*positive.*/i)]").exists());
        }

        @Test
        @DisplayName("WHEN duration is zero THEN should return 400 BAD REQUEST with validation error")
        void shouldRejectZeroDuration() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createRequestWithZeroDuration();
            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("WHEN request body is empty THEN should return 400 BAD REQUEST")
        void shouldRejectEmptyRequestBody() throws Exception {
            // GIVEN & WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("WHEN request body is malformed JSON THEN should return 500 INTERNAL SERVER ERROR")
        void shouldRejectMalformedJson() throws Exception {
            // GIVEN & WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }

    @Nested
    @DisplayName("GIVEN domain validation errors")
    class DomainValidationErrorScenarios {

        @Test
        @DisplayName("WHEN TimeRangeInvalidDurationException is thrown THEN should return 400 BAD REQUEST")
        void shouldHandleTimeRangeInvalidDurationException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new TimeRangeInvalidDurationException("durationMinutes must be > 0"));

            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("durationMinutes must be > 0"))
                    .andExpect(jsonPath("$.path").value(BASE_URL));
        }

        @Test
        @DisplayName("WHEN InvalidTimeRangeException is thrown THEN should return 400 BAD REQUEST")
        void shouldHandleInvalidTimeRangeException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new InvalidTimeRangeException("end must be after start"));

            // WHEN & THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("end must be after start"));
        }
    }

    @Nested
    @DisplayName("GIVEN business rule violations")
    class BusinessRuleViolationScenarios {

        @Test
        @DisplayName("WHEN user not found THEN should return 404 NOT FOUND")
        void shouldHandleUserNotFoundException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            String username = TimeSlotMother.defaultUsername();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new UserNotFoundException("User not found with username: " + username));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value(containsString("User not found")))
                    .andExpect(jsonPath("$.path").value(BASE_URL));
        }

        @Test
        @DisplayName("WHEN time slot collision occurs THEN should return 409 CONFLICT")
        void shouldHandleTimeSlotCollisionException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            UUID existingSlotId = UUID.randomUUID();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new TimeSlotCollisionException(
                            "time slot overlaps an existing slot: " + existingSlotId));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value(containsString("overlaps")))
                    .andExpect(jsonPath("$.path").value(BASE_URL));
        }
    }

    @Nested
    @DisplayName("GIVEN infrastructure errors")
    class InfrastructureErrorScenarios {

        @Test
        @DisplayName("WHEN TimeSlotInvalidIdException is thrown THEN should return 500 INTERNAL SERVER ERROR")
        void shouldHandleTimeSlotInvalidIdException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new TimeSlotInvalidIdException("ID collision detected"));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value(containsString("internal error")))
                    .andExpect(jsonPath("$.path").value(BASE_URL));
        }

        @Test
        @DisplayName("WHEN DataIntegrityViolationException is thrown THEN should return 500 INTERNAL SERVER ERROR")
        void shouldHandleDataIntegrityViolationException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new DataIntegrityViolationException("Database constraint violated"));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("A database constraint was violated"))
                    .andExpect(jsonPath("$.path").value(BASE_URL));
        }

        @Test
        @DisplayName("WHEN DataAccessException is thrown THEN should return 500 INTERNAL SERVER ERROR")
        void shouldHandleDataAccessException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new DataAccessException("Database connection failed") {
                    });
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An error occurred while accessing the database"));
        }

        @Test
        @DisplayName("WHEN NullPointerException is thrown THEN should return 500 INTERNAL SERVER ERROR")
        void shouldHandleNullPointerException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new NullPointerException("Unexpected null value"));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
        }

        @Test
        @DisplayName("WHEN generic Exception is thrown THEN should return 500 INTERNAL SERVER ERROR")
        void shouldHandleGenericException() throws Exception {
            // GIVEN
            CreateTimeSlotRequestDto requestDto = TimeSlotMother.createValidRequest();
            // WHEN
            when(createTimeSlotUseCase.execute(any(CreateTimeSlotCommand.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));
            // THEN
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }
    }
}
