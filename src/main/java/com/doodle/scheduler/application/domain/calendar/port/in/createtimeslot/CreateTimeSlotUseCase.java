package com.doodle.scheduler.application.domain.calendar.port.in.createtimeslot;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

public interface CreateTimeSlotUseCase {
    TimeSlot execute(CreateTimeSlotCommand command);
}
