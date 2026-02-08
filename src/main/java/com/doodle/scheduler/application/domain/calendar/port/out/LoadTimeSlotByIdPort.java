package com.doodle.scheduler.application.domain.calendar.port.out;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.util.Optional;
import java.util.UUID;

public interface LoadTimeSlotByIdPort {
    Optional<TimeSlot> loadTimeSlotById(UUID timeSlotId);
}
