package com.doodle.scheduler.application.domain.common.events;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

import java.time.Instant;

public record TimeSlotCreatedEvent(TimeSlot timeSlot, Instant timestamp) implements DomainEvent {
}
