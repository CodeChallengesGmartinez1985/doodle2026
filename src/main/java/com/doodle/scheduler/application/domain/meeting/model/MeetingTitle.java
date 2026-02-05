package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.common.model.ValueObject;

import java.util.Objects;

public record MeetingTitle(String value) implements ValueObject {
    public MeetingTitle {
        Objects.requireNonNull(value, "title must not be null");
        value = value.trim();
        if (value.isBlank()) throw new IllegalArgumentException("title must not be blank");
    }
}
