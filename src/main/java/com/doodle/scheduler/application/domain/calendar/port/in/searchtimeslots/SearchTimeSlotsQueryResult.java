package com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.util.List;

public record SearchTimeSlotsQueryResult(
        List<TimeSlot> timeSlots,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
}
