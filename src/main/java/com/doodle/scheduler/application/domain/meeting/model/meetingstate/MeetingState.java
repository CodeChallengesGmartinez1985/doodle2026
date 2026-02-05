package com.doodle.scheduler.application.domain.meeting.model.meetingstate;

import com.doodle.scheduler.application.domain.meeting.exception.InvalidMeetingStateTransitionException;

public abstract class MeetingState {

    public boolean isScheduled() {
        return false;
    }

    protected InvalidMeetingStateTransitionException invalid(String op) {
        return new InvalidMeetingStateTransitionException("Cannot " + op + " from " + this.getClass().getSimpleName());
    }
}
