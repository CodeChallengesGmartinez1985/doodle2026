package com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots;

public interface SearchTimeSlotsUseCase {
    SearchTimeSlotsQueryResult execute(SearchTimeSlotsCommand command);
}
