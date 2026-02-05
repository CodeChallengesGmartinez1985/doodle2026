package com.doodle.scheduler.application.domain.meeting.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class MeetingWithoutParticipantsException extends DomainException {
    public MeetingWithoutParticipantsException(String message) {
        super(message);
    }
}
