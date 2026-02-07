package com.doodle.scheduler.application.adapter.out.persistence.common;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeSlotJpaMapper {

    default TimeSlot toDomain(TimeSlotJpaEntity entity) {
        if (entity == null) return null;
        return TimeSlot.create(entity.getId(), entity.getStartTime(), entity.getDurationMinutes());
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "ownerId", expression = "java(timeSlot.getOwnerId())")
    @Mapping(target = "startTime", source = "range.start")
    @Mapping(target = "endTime", source = "range.end")
    @Mapping(target = "durationMinutes", expression = "java((int) timeSlot.getDurationMinutes())")
    @Mapping(target = "state", expression = "java(timeSlot.getStateString())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TimeSlotJpaEntity toJpaEntity(TimeSlot timeSlot);
}


