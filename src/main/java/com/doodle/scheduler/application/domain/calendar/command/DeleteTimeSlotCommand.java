package com.doodle.scheduler.application.domain.calendar.command;

import java.util.UUID;

public record DeleteTimeSlotCommand(
        String username,
        UUID timeSlotId
) {
}
