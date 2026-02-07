package com.doodle.scheduler.application.config.usecase.createtimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.commands.calendar.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.port.in.CreateTimeSlotUseCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedCreateTimeSlotUseCaseDecorator extends BaseCreateTimeSlotUseCaseDecorator {

    public LoggedCreateTimeSlotUseCaseDecorator(CreateTimeSlotUseCase delegate) {
        super(delegate);
    }

    @Override
    public TimeSlot execute(CreateTimeSlotCommand command) {
        log.info("Executing AddTimeSlotUseCase for username={}, start={}, durationMinutes={}",
                command.username(), command.start(), command.durationMinutes());

        try {
            TimeSlot result = super.execute(command);
            log.info("Successfully added time slot with id={}, state={} for username={}",
                    result.getId(), result.getStateString(), command.username());
            return result;
        } catch (Exception e) {
            log.error("Error adding time slot for username={}: {}", command.username(), e.getMessage(), e);
            throw e;
        }
    }
}
