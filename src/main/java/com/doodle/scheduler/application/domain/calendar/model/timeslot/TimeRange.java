package com.doodle.scheduler.application.domain.calendar.model.timeslot;

import com.doodle.scheduler.application.domain.common.model.ValueObject;
import com.doodle.scheduler.application.domain.calendar.exception.InvalidTimeRangeException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeRangeInvalidDurationException;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record TimeRange(Instant start, Instant end) implements ValueObject {
    /**
     * Public API
     */
    public TimeRange {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) throw new InvalidTimeRangeException("end must be after start");
    }

    public static TimeRange of(Instant start, int durationMinutes) {
        Objects.requireNonNull(start, "start must not be null");
        if (durationMinutes <= 0) throw new TimeRangeInvalidDurationException("durationMinutes must be > 0");
        Instant end = start.plus(Duration.ofMinutes(durationMinutes));
        return new TimeRange(start, end);
    }
}
