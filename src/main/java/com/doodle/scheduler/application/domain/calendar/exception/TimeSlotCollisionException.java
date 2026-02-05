package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class TimeSlotCollisionException extends DomainException {
    public TimeSlotCollisionException(String message) {
        super(message);
    }
}
