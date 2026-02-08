package com.doodle.scheduler.application.domain.calendar.port.out;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SearchTimeSlotsPort {
    SearchResult searchTimeSlots(UUID ownerId, String status, Instant startTime, Instant endTime, int page, int size);

    record SearchResult(
            List<TimeSlot> timeSlots,
            long totalElements
    ) {}
}
