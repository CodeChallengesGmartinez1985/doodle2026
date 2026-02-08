package com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseDeleteTimeSlotUseCaseDecorator implements DeleteTimeSlotUseCase {

    protected final DeleteTimeSlotUseCase delegate;

    @Override
    public void execute(DeleteTimeSlotCommand command) {
        delegate.execute(command);
    }
}
