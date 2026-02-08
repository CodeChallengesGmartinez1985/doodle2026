package com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.command.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.DeleteTimeSlotUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalDeleteTimeSlotUseCaseDecorator extends BaseDeleteTimeSlotUseCaseDecorator {

    public TransactionalDeleteTimeSlotUseCaseDecorator(DeleteTimeSlotUseCase delegate) {
        super(delegate);
    }

    @Override
    @Transactional
    public void execute(DeleteTimeSlotCommand command) {
        super.execute(command);
    }
}
