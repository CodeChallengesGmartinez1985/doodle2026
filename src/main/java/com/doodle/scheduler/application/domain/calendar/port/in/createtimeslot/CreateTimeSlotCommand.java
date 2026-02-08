package com.doodle.scheduler.application.domain.calendar.port.in.createtimeslot;

import java.time.Instant;

public record CreateTimeSlotCommand(
        String username,
        Instant start,
        int durationMinutes
) {
}
