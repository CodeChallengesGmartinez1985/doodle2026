package com.doodle.scheduler.application.domain.common.events;

public interface Subscriber <E extends DomainEvent> {
    void update(E event);
}
