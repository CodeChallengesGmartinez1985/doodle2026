package com.doodle.scheduler.application.config.usecase.createtimeslot.decorators;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.commands.calendar.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.port.in.CreateTimeSlotUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseCreateTimeSlotUseCaseDecorator implements CreateTimeSlotUseCase {

    protected final CreateTimeSlotUseCase delegate;

    @Override
    public TimeSlot execute(CreateTimeSlotCommand command) {
        return delegate.execute(command);
    }
}
