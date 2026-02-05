package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class TimeSlotNotFoundException extends DomainException {
    public TimeSlotNotFoundException(String message) {
        super(message);
    }
}
