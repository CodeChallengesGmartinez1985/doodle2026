package com.doodle.scheduler.application.domain.calendar.port.out;

import java.util.UUID;

public interface DeleteTimeSlotPort {
    void deleteTimeSlot(UUID timeSlotId);
}
