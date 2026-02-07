package com.doodle.scheduler.application.domain.meeting.model.meetingstate;

public final class ScheduledState extends MeetingState {
    private ScheduledState() {}

    public static final ScheduledState INSTANCE = new ScheduledState();

    @Override
    public boolean isScheduled() {
        return true;
    }

    @Override
    public String getStateString() {
        return "SCHEDULED";
    }
}
