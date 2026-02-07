package com.doodle.scheduler.application.domain.port.out.timeslot;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;

public interface LoadTimeSlotsByUserPort {
    List<TimeSlot> loadTimeSlotsByUserId(UUID userId);
}
