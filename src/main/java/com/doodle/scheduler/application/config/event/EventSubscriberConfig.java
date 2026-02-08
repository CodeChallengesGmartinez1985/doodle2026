package com.doodle.scheduler.application.config.event;

import com.doodle.scheduler.application.adapter.in.event.TimeSlotCreatedListener;
import com.doodle.scheduler.application.domain.common.events.Publisher;
import com.doodle.scheduler.application.domain.common.events.TimeSlotCreatedEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventSubscriberConfig {

    private final TimeSlotCreatedListener timeSlotCreatedListener;

    @PostConstruct
    public void registerSubscribers() {
        log.info("Registering domain event subscribers...");

        Publisher.INSTANCE.attach(TimeSlotCreatedEvent.class, timeSlotCreatedListener);

        log.info("Domain event subscribers registered successfully");
    }
}
