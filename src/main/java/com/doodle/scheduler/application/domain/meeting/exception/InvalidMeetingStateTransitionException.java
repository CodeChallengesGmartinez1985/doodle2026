package com.doodle.scheduler.application.domain.meeting.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class InvalidMeetingStateTransitionException extends DomainException {
    public InvalidMeetingStateTransitionException(String message) {
        super(message);
    }
}
