package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.objectmother;

import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.CreateTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.commands.calendar.CreateTimeSlotCommand;

import java.time.Instant;
import java.util.UUID;

/**
 * Object Mother pattern for creating TimeSlot-related test data.
 * Provides factory methods with sensible defaults to reduce test boilerplate.
 */
public class TimeSlotMother {

    private static final String DEFAULT_USERNAME = "authenticated-user";
    private static final Instant DEFAULT_START = Instant.parse("2026-02-07T10:00:00Z");
    private static final int DEFAULT_DURATION_MINUTES = 60;
    private static final UUID DEFAULT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    // CreateTimeSlotRequestDto
    public static CreateTimeSlotRequestDto createValidRequest() {
        return new CreateTimeSlotRequestDto(DEFAULT_START, DEFAULT_DURATION_MINUTES);
    }

    public static CreateTimeSlotRequestDto createRequestWithNullStart() {
        return new CreateTimeSlotRequestDto(null, DEFAULT_DURATION_MINUTES);
    }

    public static CreateTimeSlotRequestDto createRequestWithNullDuration() {
        return new CreateTimeSlotRequestDto(DEFAULT_START, null);
    }

    public static CreateTimeSlotRequestDto createRequestWithNegativeDuration() {
        return new CreateTimeSlotRequestDto(DEFAULT_START, -30);
    }

    public static CreateTimeSlotRequestDto createRequestWithZeroDuration() {
        return new CreateTimeSlotRequestDto(DEFAULT_START, 0);
    }

    public static CreateTimeSlotRequestDto createRequestWith(Instant start, Integer durationMinutes) {
        return new CreateTimeSlotRequestDto(start, durationMinutes);
    }

    // CreateTimeSlotCommand
    public static CreateTimeSlotCommand createValidCommand() {
        return new CreateTimeSlotCommand(DEFAULT_USERNAME, DEFAULT_START, DEFAULT_DURATION_MINUTES);
    }

    public static CreateTimeSlotCommand createCommandWith(String username, Instant start, int durationMinutes) {
        return new CreateTimeSlotCommand(username, start, durationMinutes);
    }

    // TimeSlot (domain)
    public static TimeSlot createValidTimeSlot() {
        return TimeSlot.create(DEFAULT_ID, DEFAULT_START, DEFAULT_DURATION_MINUTES);
    }

    public static TimeSlot createTimeSlotWith(UUID id, Instant start, int durationMinutes) {
        return TimeSlot.create(id, start, durationMinutes);
    }

    // TimeSlotResponseDto
    public static TimeSlotResponseDto createValidResponseDto() {
        Instant end = DEFAULT_START.plusSeconds(DEFAULT_DURATION_MINUTES * 60L);
        return new TimeSlotResponseDto(DEFAULT_ID, DEFAULT_START, end, DEFAULT_DURATION_MINUTES, "AVAILABLE");
    }

    public static TimeSlotResponseDto createResponseDtoWith(UUID id, Instant start, Instant end, Integer durationMinutes, String state) {
        return new TimeSlotResponseDto(id, start, end, durationMinutes, state);
    }

    // Constants for reuse
    public static String defaultUsername() {
        return DEFAULT_USERNAME;
    }

    public static Instant defaultStart() {
        return DEFAULT_START;
    }

    public static int defaultDurationMinutes() {
        return DEFAULT_DURATION_MINUTES;
    }

    public static UUID defaultId() {
        return DEFAULT_ID;
    }

    private TimeSlotMother() {
        // Utility class
    }
}
