package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.common.model.ValueObject;

import java.util.Objects;

public record MeetingDescription(String value) implements ValueObject {
    public MeetingDescription {
        Objects.requireNonNull(value, "description must not be null");
    }
}
