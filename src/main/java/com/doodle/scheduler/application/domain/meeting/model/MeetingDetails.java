package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.common.model.ValueObject;
import com.doodle.scheduler.application.domain.meeting.exception.MeetingWithoutParticipantsException;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record MeetingDetails(
        MeetingTitle meetingTitle,
        MeetingDescription meetingDescription,
        Set<UUID> participants
) implements ValueObject {
    /**
     * Public API
     */
    public MeetingDetails {
        Objects.requireNonNull(meetingTitle, "title must not be null");
        Objects.requireNonNull(meetingDescription, "description must not be null");
        Objects.requireNonNull(participants, "participants must not be null");
        if (participants.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("participants must not contain null");
        }
        if (participants.isEmpty()) throw new MeetingWithoutParticipantsException("participants must not be empty");
        participants = Set.copyOf(participants);
    }
}
