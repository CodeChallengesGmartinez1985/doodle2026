package com.doodle.scheduler.application.domain.calendar.port.in;

import com.doodle.scheduler.application.domain.calendar.command.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.util.List;

public interface SearchTimeSlotsUseCase {
    SearchTimeSlotsResult execute(SearchTimeSlotsCommand command);

    record SearchTimeSlotsResult(
            List<TimeSlot> timeSlots,
            long totalElements,
            int totalPages,
            int currentPage,
            int pageSize
    ) {}
}
