package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class TimeSlotNotAvailableException extends DomainException {
    public TimeSlotNotAvailableException(String message) {
        super(message);
    }
}
