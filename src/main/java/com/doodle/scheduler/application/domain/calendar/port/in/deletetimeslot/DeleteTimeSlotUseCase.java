package com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot;

public interface DeleteTimeSlotUseCase {
    void execute(DeleteTimeSlotCommand command);
}
