package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class InvalidTimeRangeException extends DomainException {
    public InvalidTimeRangeException(String message) {
        super(message);
    }
}
