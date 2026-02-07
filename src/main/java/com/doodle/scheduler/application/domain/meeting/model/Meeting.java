package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.common.model.Entity;
import com.doodle.scheduler.application.domain.meeting.exception.MeetingCreationException;
import com.doodle.scheduler.application.domain.meeting.model.meetingstate.MeetingState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Meeting extends Entity {
    private final MeetingDetails details;
    private final List<UUID> slotIds = new ArrayList<>();
    private MeetingState state;

    public static Meeting create(MeetingDetails details, List<UUID> slotIds) {
        Objects.requireNonNull(details, "details must not be null");
        Objects.requireNonNull(slotIds, "slotIds must not be null");
        if (slotIds.isEmpty()) throw new MeetingCreationException("slotIds must not be empty");
        if (slotIds.size() != 1) throw new MeetingCreationException("Meeting must have exactly 1 slot, but got " + slotIds.size());
        return new Meeting(UUID.randomUUID(), details, List.copyOf(slotIds), com.doodle.scheduler.application.domain.meeting.model.meetingstate.ScheduledState.INSTANCE);
    }

    public UUID getSlotId() {
        return slotIds.get(0);
    }

    public MeetingDetails getDetails() {
        return details;
    }

    public MeetingState getState() {
        return state;
    }

    public String getStateString() {
        return state.getStateString();
    }

    public String getTitle() {
        return details.meetingTitle().value();
    }

    public String getDescription() {
        return details.meetingDescription().value();
    }

    private Meeting(UUID id, MeetingDetails details, List<UUID> slotIds, com.doodle.scheduler.application.domain.meeting.model.meetingstate.MeetingState initialState) {
        super(id);
        this.details = details;
        this.slotIds.addAll(slotIds);
        this.state = initialState;
    }
}
