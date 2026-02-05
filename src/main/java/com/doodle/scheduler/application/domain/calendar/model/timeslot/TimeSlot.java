package com.doodle.scheduler.application.domain.calendar.model.timeslot;

import com.doodle.scheduler.application.domain.common.model.Entity;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.state.AvailableState;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.state.SlotState;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TimeSlot extends Entity {
    /**
     * Attributes
     */
    private TimeRange range;
    private SlotState state;

    /**
     * Public API
     */
    public static TimeSlot create(UUID id, Instant start, int durationMinutes) {
        Objects.requireNonNull(id, "id must not be null");
        TimeRange range = TimeRange.of(start, durationMinutes);
        return new TimeSlot(id, range, AvailableState.INSTANCE);
    }

    public TimeRange getRange() {
        return range;
    }

    public SlotState getState() {
        return state;
    }

    public void changeTimeRange(Instant start, int durationMinutes) {
        this.range = TimeRange.of(start, durationMinutes);
    }

    public void markBusy() {
        this.state = state.markBusy();
    }

    public void markAvailable() {
        this.state = state.markAvailable();
    }

    /**
     * Private methods / constructors
     */
    private TimeSlot(UUID id, TimeRange range, SlotState initialState) {
        super(id);
        this.range = range;
        this.state = initialState;
    }
}
