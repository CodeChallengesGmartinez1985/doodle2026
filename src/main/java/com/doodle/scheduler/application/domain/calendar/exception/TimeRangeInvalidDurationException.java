package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class TimeRangeInvalidDurationException extends DomainException {
    public TimeRangeInvalidDurationException(String message) {
        super(message);
    }
}
