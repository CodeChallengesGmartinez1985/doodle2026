package com.doodle.scheduler.application.domain.common.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Publisher {
    INSTANCE;
    private final Map<Class<? extends DomainEvent>, List<Subscriber<? extends DomainEvent>>> subscribers = new HashMap<>();

    public <T extends DomainEvent> void attach(Class<T> eventType, Subscriber<T> observer) {
        this.subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(observer);
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void notifyObservers(T event) {
        if (event == null) {
            return;
        }
        final List<Subscriber<? extends DomainEvent>> list = this.subscribers.getOrDefault(event.getClass(), List.of());
        for (final Subscriber<? extends DomainEvent> obs : list) {
            ((Subscriber<T>) obs).update(event);
        }
    }
}
