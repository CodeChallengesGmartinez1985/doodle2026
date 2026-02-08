package com.doodle.scheduler.application.domain.calendar.port.out.searchtimeslots;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.util.List;
import java.util.UUID;

public interface LoadTimeSlotsByUserPort {
    List<TimeSlot> loadTimeSlotsByUserId(UUID userId);
}
