package com.doodle.scheduler.application.domain.calendar.command;

import java.time.Instant;

public record SearchTimeSlotsCommand(
        String username,
        String status,
        Instant startTime,
        Instant endTime,
        int page,
        int size
) {
}
