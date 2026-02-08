package com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotUseCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedDeleteTimeSlotUseCaseDecorator extends BaseDeleteTimeSlotUseCaseDecorator {

    public LoggedDeleteTimeSlotUseCaseDecorator(DeleteTimeSlotUseCase delegate) {
        super(delegate);
    }

    @Override
    public void execute(DeleteTimeSlotCommand command) {
        log.info("Executing DeleteTimeSlotUseCase for username={}, timeSlotId={}",
                command.username(), command.timeSlotId());

        try {
            super.execute(command);
            log.info("Successfully deleted time slot with id={} for username={}",
                    command.timeSlotId(), command.username());
        } catch (Exception e) {
            log.error("Error deleting time slot with id={} for username={}: {}",
                    command.timeSlotId(), command.username(), e.getMessage(), e);
            throw e;
        }
    }
}
