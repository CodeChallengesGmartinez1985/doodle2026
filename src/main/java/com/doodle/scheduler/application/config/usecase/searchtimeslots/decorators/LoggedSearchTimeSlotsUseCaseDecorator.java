package com.doodle.scheduler.application.config.usecase.searchtimeslots.decorators;

import com.doodle.scheduler.application.domain.calendar.command.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.SearchTimeSlotsUseCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggedSearchTimeSlotsUseCaseDecorator extends BaseSearchTimeSlotsUseCaseDecorator {

    public LoggedSearchTimeSlotsUseCaseDecorator(SearchTimeSlotsUseCase delegate) {
        super(delegate);
    }

    @Override
    public SearchTimeSlotsResult execute(SearchTimeSlotsCommand command) {
        log.info("Executing SearchTimeSlotsUseCase for username={}, status={}, startTime={}, endTime={}, page={}, size={}",
                command.username(), command.status(), command.startTime(), command.endTime(), command.page(), command.size());

        try {
            SearchTimeSlotsResult result = super.execute(command);
            log.info("Successfully searched time slots for username={}. Found {} elements, page {}/{}",
                    command.username(), result.totalElements(), result.currentPage() + 1, result.totalPages());
            return result;
        } catch (Exception e) {
            log.error("Error searching time slots for username={}: {}", command.username(), e.getMessage(), e);
            throw e;
        }
    }
}
