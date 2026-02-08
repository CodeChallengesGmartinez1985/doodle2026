package com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.command.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.DeleteTimeSlotUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseDeleteTimeSlotUseCaseDecorator implements DeleteTimeSlotUseCase {

    protected final DeleteTimeSlotUseCase delegate;

    @Override
    public void execute(DeleteTimeSlotCommand command) {
        delegate.execute(command);
    }
}
