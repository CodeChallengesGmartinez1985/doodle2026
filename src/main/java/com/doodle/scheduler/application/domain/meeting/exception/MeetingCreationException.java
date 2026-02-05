package com.doodle.scheduler.application.domain.meeting.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class MeetingCreationException extends DomainException {
    public MeetingCreationException(String message) {
        super(message);
    }
}
