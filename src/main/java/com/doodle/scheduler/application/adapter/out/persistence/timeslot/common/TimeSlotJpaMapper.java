package com.doodle.scheduler.application.adapter.out.persistence.timeslot.common;

import com.doodle.scheduler.application.domain.calendar.model.Calendar;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeSlotJpaMapper {

    default TimeSlot toDomain(TimeSlotJpaEntity entity) {
        if (entity == null) return null;
        TimeSlot timeSlot = TimeSlot.create(entity.getId(), entity.getStartTime(), entity.getDurationMinutes());
        Calendar calendar = Calendar.create(entity.getOwnerId());
        timeSlot.setCalendar(calendar);
        return timeSlot;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "ownerId", expression = "java(timeSlot.getOwnerId())")
    @Mapping(target = "startTime", source = "range.start")
    @Mapping(target = "endTime", source = "range.end")
    @Mapping(target = "durationMinutes", expression = "java((int) timeSlot.getDurationMinutes())")
    @Mapping(target = "state", expression = "java(timeSlot.getStateString())")
    TimeSlotJpaEntity toJpaEntity(TimeSlot timeSlot);
}
