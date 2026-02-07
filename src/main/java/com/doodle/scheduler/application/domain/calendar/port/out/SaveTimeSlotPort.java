package com.doodle.scheduler.application.domain.calendar.port.out;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

public interface SaveTimeSlotPort {
    TimeSlot saveTimeSlot(TimeSlot timeSlot);
}
