package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class InvalidSlotStateTransitionException extends DomainException {
    public InvalidSlotStateTransitionException(String message) {
        super(message);
    }
}
