package com.doodle.scheduler.application.domain.calendar.service;

import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsQueryResult;
import com.doodle.scheduler.application.domain.calendar.port.in.searchtimeslots.SearchTimeSlotsUseCase;
import com.doodle.scheduler.application.domain.calendar.port.out.SearchTimeSlotsPort;
import com.doodle.scheduler.application.domain.user.model.User;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;

import java.util.List;
import java.util.UUID;

public class SearchTimeSlotsServiceImpl implements SearchTimeSlotsUseCase {

    private final LoadUserByUsernamePort loadUserByUsernamePort;
    private final SearchTimeSlotsPort searchTimeSlotsPort;

    public SearchTimeSlotsServiceImpl(LoadUserByUsernamePort loadUserByUsernamePort,
                                      SearchTimeSlotsPort searchTimeSlotsPort) {
        this.loadUserByUsernamePort = loadUserByUsernamePort;
        this.searchTimeSlotsPort = searchTimeSlotsPort;
    }

    @Override
    public SearchTimeSlotsQueryResult execute(SearchTimeSlotsCommand command) {
        User user = loadUserByUsernamePort.loadUserByUsername(command.username());
        UUID userId = user.getId();

        SearchTimeSlotsPort.SearchResult searchResult = searchTimeSlotsPort.searchTimeSlots(
                userId,
                command.status(),
                command.startTime(),
                command.endTime(),
                command.page(),
                command.size()
        );

        List<TimeSlot> timeSlots = searchResult.timeSlots();
        long totalElements = searchResult.totalElements();
        int totalPages = (int) Math.ceil((double) totalElements / command.size());

        return new SearchTimeSlotsQueryResult(
                timeSlots,
                totalElements,
                totalPages,
                command.page(),
                command.size()
        );
    }
}
