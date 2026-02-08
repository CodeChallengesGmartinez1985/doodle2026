package com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators;

import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsQueryResult;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseSearchTimeSlotsUseCaseDecorator implements SearchTimeSlotsUseCase {

    protected final SearchTimeSlotsUseCase delegate;

    @Override
    public SearchTimeSlotsQueryResult execute(SearchTimeSlotsCommand command) {
        return delegate.execute(command);
    }
}
