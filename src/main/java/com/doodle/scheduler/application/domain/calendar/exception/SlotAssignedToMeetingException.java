package com.doodle.scheduler.application.domain.calendar.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class SlotAssignedToMeetingException extends DomainException {
    public SlotAssignedToMeetingException(String message) {
        super(message);
    }
}
