package com.doodle.scheduler.application.domain.calendar.port.in;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.command.CreateTimeSlotCommand;

public interface CreateTimeSlotUseCase {
    TimeSlot execute(CreateTimeSlotCommand command);
}
