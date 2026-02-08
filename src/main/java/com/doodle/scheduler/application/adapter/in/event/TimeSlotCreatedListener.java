package com.doodle.scheduler.application.adapter.in.event;

import com.doodle.scheduler.application.domain.common.events.Subscriber;
import com.doodle.scheduler.application.domain.common.events.TimeSlotCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class TimeSlotCreatedListener implements Subscriber<TimeSlotCreatedEvent> {

    @Override
    public void update(TimeSlotCreatedEvent event) {
        long durationMinutes = Duration.between(
                event.timeSlot().getRange().start(),
                event.timeSlot().getRange().end()
        ).toMinutes();

        log.info("TimeSlotCreatedEvent received: TimeSlot ID={}, Owner ID={}, Start={}, Duration={} minutes, Timestamp={}",
                event.timeSlot().getId(),
                event.timeSlot().getOwnerId(),
                event.timeSlot().getRange().start(),
                durationMinutes,
                event.timestamp());
    }
}
