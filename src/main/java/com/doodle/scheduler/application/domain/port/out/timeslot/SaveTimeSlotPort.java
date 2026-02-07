package com.doodle.scheduler.application.domain.port.out.timeslot;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;

public interface SaveTimeSlotPort {
    TimeSlot saveTimeSlot(TimeSlot timeSlot);
}
