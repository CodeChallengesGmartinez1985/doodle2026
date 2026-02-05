package com.doodle.scheduler.application.domain.common.model;

import java.util.UUID;

/**
 * Marker/ base class for domain Entities — provides a canonical identity (UUID) for domain objects.
 */
public abstract class Entity {
    private final UUID id;

    protected Entity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
