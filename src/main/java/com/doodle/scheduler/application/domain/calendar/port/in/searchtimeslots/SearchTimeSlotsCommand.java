package com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots;

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
