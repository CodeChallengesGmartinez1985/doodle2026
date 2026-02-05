package com.doodle.scheduler.application.domain.common.model;

import java.util.UUID;

/**
 * Marker for Aggregate Roots in the domain. Kept as a separate type to signal intent even when
 * aggregate roots share the same identity handling as entities.
 */
public abstract class AggregateRoot {
    private final UUID id;

    protected AggregateRoot(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
