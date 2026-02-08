package com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot;

import java.util.UUID;

public record DeleteTimeSlotCommand(
        String username,
        UUID timeSlotId
) {
}
