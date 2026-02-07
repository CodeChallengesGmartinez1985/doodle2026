package com.doodle.scheduler.application.adapter.out.persistence.meeting.common;

import com.doodle.scheduler.application.domain.meeting.model.Meeting;
import com.doodle.scheduler.application.domain.meeting.model.MeetingDetails;
import com.doodle.scheduler.application.domain.meeting.model.MeetingTitle;
import com.doodle.scheduler.application.domain.meeting.model.MeetingDescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MeetingJpaMapper {

    default Meeting toDomain(MeetingJpaEntity entity) {
        if (entity == null) return null;
        MeetingTitle title = new MeetingTitle(entity.getTitle());
        MeetingDescription description = new MeetingDescription(
            entity.getDescription() != null ? entity.getDescription() : ""
        );
        Set<UUID> participants = new HashSet<>();
        MeetingDetails details = new MeetingDetails(title, description, participants);
        return Meeting.create(details, List.of(entity.getTimeSlotId()));
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", expression = "java(meeting.getTitle())")
    @Mapping(target = "description", expression = "java(meeting.getDescription())")
    @Mapping(target = "timeSlotId", expression = "java(meeting.getSlotId())")
    @Mapping(target = "state", expression = "java(meeting.getStateString())")
    MeetingJpaEntity toJpaEntity(Meeting meeting);
}
