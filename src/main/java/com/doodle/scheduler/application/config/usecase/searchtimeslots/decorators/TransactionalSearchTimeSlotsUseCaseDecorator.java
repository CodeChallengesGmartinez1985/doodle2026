package com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators;

import com.doodle.scheduler.application.domain.calendar.command.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.SearchTimeSlotsUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalSearchTimeSlotsUseCaseDecorator extends BaseSearchTimeSlotsUseCaseDecorator {

    public TransactionalSearchTimeSlotsUseCaseDecorator(SearchTimeSlotsUseCase delegate) {
        super(delegate);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchTimeSlotsResult execute(SearchTimeSlotsCommand command) {
        return super.execute(command);
    }
}
