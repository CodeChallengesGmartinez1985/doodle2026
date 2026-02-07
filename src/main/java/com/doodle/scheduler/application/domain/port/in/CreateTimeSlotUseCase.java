package com.doodle.scheduler.application.domain.port.in;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.commands.calendar.CreateTimeSlotCommand;

public interface CreateTimeSlotUseCase {
    TimeSlot execute(CreateTimeSlotCommand command);
}
