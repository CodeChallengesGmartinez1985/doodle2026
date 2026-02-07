package com.doodle.scheduler.application.domain.calendar.command;

import java.time.Instant;

public record CreateTimeSlotCommand(
        String username,
        Instant start,
        int durationMinutes
) {
}
