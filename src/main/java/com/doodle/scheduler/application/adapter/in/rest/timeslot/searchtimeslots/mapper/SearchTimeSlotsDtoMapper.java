package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.mapper;

import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsResponseDto;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.in.SearchTimeSlotsUseCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SearchTimeSlotsDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "start", source = "range.start")
    @Mapping(target = "end", source = "range.end")
    @Mapping(target = "durationMinutes", expression = "java((int) timeSlot.getDurationMinutes())")
    @Mapping(target = "state", source = "stateString")
    TimeSlotResponseDto toResponseDto(TimeSlot timeSlot);

    default SearchTimeSlotsResponseDto toSearchResponseDto(SearchTimeSlotsUseCase.SearchTimeSlotsResult result) {
        List<TimeSlotResponseDto> timeSlotDtos = result.timeSlots().stream()
                .map(this::toResponseDto)
                .toList();

        return new SearchTimeSlotsResponseDto(
                timeSlotDtos,
                result.totalElements(),
                result.totalPages(),
                result.currentPage(),
                result.pageSize()
        );
    }
}
