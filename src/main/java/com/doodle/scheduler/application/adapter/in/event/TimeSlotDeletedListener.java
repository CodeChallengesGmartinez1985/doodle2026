package com.doodle.scheduler.application.adapter.in.event;

import com.doodle.scheduler.application.domain.common.events.Subscriber;
import com.doodle.scheduler.application.domain.common.events.TimeSlotDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimeSlotDeletedListener implements Subscriber<TimeSlotDeletedEvent> {

    @Override
    public void update(TimeSlotDeletedEvent event) {
        log.info("TimeSlotDeletedEvent received: TimeSlot ID={}, Owner ID={}, Timestamp={}",
                event.timeSlotId(),
                event.ownerId(),
                event.timestamp());
    }
}
