package com.doodle.scheduler.application.domain.commands.calendar;

import java.time.Instant;

public record CreateTimeSlotCommand(
        String username,
        Instant start,
        int durationMinutes
) {
}
