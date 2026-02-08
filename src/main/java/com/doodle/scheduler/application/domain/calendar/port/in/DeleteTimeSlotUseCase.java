package com.doodle.scheduler.application.domain.calendar.port.in;

import com.doodle.scheduler.application.domain.calendar.command.DeleteTimeSlotCommand;

public interface DeleteTimeSlotUseCase {
    void execute(DeleteTimeSlotCommand command);
}
