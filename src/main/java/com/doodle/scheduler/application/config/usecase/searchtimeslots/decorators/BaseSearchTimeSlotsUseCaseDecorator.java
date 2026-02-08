package com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators;

import com.doodle.scheduler.application.domain.calendar.command.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.SearchTimeSlotsUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseSearchTimeSlotsUseCaseDecorator implements SearchTimeSlotsUseCase {

    protected final SearchTimeSlotsUseCase delegate;

    @Override
    public SearchTimeSlotsResult execute(SearchTimeSlotsCommand command) {
        return delegate.execute(command);
    }
}
