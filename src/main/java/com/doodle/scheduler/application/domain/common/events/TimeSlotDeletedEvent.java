package com.doodle.scheduler.application.domain.common.events;

import java.time.Instant;
import java.util.UUID;

public record TimeSlotDeletedEvent(
        UUID timeSlotId,
        UUID ownerId,
        Instant timestamp
) implements DomainEvent {
}
