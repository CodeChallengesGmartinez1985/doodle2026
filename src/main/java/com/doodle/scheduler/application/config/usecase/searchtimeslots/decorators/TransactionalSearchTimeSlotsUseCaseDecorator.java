package com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators;

import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsQueryResult;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalSearchTimeSlotsUseCaseDecorator extends BaseSearchTimeSlotsUseCaseDecorator {

    public TransactionalSearchTimeSlotsUseCaseDecorator(SearchTimeSlotsUseCase delegate) {
        super(delegate);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchTimeSlotsQueryResult execute(SearchTimeSlotsCommand command) {
        return super.execute(command);
    }
}
