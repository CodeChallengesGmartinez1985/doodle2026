package com.doodle.scheduler.application.config.usecase.createtimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.command.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.CreateTimeSlotUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalCreateTimeSlotUseCaseDecorator extends BaseCreateTimeSlotUseCaseDecorator {

    public TransactionalCreateTimeSlotUseCaseDecorator(CreateTimeSlotUseCase delegate) {
        super(delegate);
    }

    @Override
    @Transactional
    public TimeSlot execute(CreateTimeSlotCommand command) {
        return super.execute(command);
    }
}
