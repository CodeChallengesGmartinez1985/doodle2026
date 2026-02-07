package com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.mapper;

import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeSlotDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "range.start")
    @Mapping(target = "end", source = "range.end")
    @Mapping(target = "durationMinutes", expression = "java((int) timeSlot.getDurationMinutes())")
    @Mapping(target = "state", source = "stateString")
    TimeSlotResponseDto toResponseDto(TimeSlot timeSlot);
}
