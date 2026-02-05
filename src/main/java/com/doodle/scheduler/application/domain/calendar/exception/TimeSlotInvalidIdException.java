package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class TimeSlotInvalidIdException extends DomainException {
    public TimeSlotInvalidIdException(String message) {
        super(message);
    }
}
