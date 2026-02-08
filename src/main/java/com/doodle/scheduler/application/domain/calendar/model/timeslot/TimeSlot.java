package com.doodle.scheduler.application.domain.calendar.model.timeslot;

import com.doodle.scheduler.application.domain.common.model.Entity;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.state.AvailableState;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.state.SlotState;
import com.doodle.scheduler.application.domain.calendar.model.Calendar;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TimeSlot extends Entity {
    /**
     * Attributes
     */
    private TimeRange range;
    private SlotState state;
    private Calendar calendar;

    /**
     * Public API
     */
    public static TimeSlot create(UUID id, Instant start, int durationMinutes) {
        Objects.requireNonNull(id, "id must not be null");
        TimeRange range = TimeRange.of(start, durationMinutes);
        return new TimeSlot(id, range, AvailableState.INSTANCE);
    }

    public static TimeSlot reconstitute(UUID id, UUID ownerId, Instant start, int durationMinutes, String stateString) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(ownerId, "ownerId must not be null");
        Objects.requireNonNull(stateString, "stateString must not be null");

        TimeRange range = TimeRange.of(start, durationMinutes);
        SlotState state = SlotState.fromString(stateString);
        Calendar calendar = Calendar.create(ownerId);

        TimeSlot timeSlot = new TimeSlot(id, range, state);
        timeSlot.setCalendar(calendar);
        return timeSlot;
    }

    public TimeRange getRange() {
        return range;
    }

    public SlotState getState() {
        return state;
    }

    public String getStateString() {
        return state.getStateString();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public UUID getOwnerId() {
        return calendar != null ? calendar.getOwnerId() : null;
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

    public long getDurationMinutes() {
        long durationMillis = range.end().toEpochMilli() - range.start().toEpochMilli();
        return durationMillis / 60_000;
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
