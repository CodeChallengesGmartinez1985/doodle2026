package com.doodle.scheduler.application.domain.calendar.service;

import com.doodle.scheduler.application.domain.calendar.command.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotFoundException;
import com.doodle.scheduler.application.domain.calendar.model.Calendar;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.in.DeleteTimeSlotUseCase;
import com.doodle.scheduler.application.domain.calendar.port.out.DeleteTimeSlotPort;
import com.doodle.scheduler.application.domain.calendar.port.out.LoadTimeSlotByIdPort;
import com.doodle.scheduler.application.domain.common.events.Publisher;
import com.doodle.scheduler.application.domain.common.events.TimeSlotDeletedEvent;
import com.doodle.scheduler.application.domain.user.model.User;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DeleteTimeSlotServiceImpl implements DeleteTimeSlotUseCase {

    private final LoadUserByUsernamePort loadUserByUsernamePort;
    private final LoadTimeSlotByIdPort loadTimeSlotByIdPort;
    private final DeleteTimeSlotPort deleteTimeSlotPort;

    public DeleteTimeSlotServiceImpl(
            LoadUserByUsernamePort loadUserByUsernamePort,
            LoadTimeSlotByIdPort loadTimeSlotByIdPort,
            DeleteTimeSlotPort deleteTimeSlotPort) {
        this.loadUserByUsernamePort = loadUserByUsernamePort;
        this.loadTimeSlotByIdPort = loadTimeSlotByIdPort;
        this.deleteTimeSlotPort = deleteTimeSlotPort;
    }

    @Override
    public void execute(DeleteTimeSlotCommand command) {
        User user = loadUserByUsernamePort.loadUserByUsername(command.username());
        UUID userId = user.getId();
        TimeSlot timeSlot = loadTimeSlotByIdPort.loadTimeSlotById(command.timeSlotId())
                .orElseThrow(() -> new TimeSlotNotFoundException(
                        "Time slot not found with id: " + command.timeSlotId()));
        if (!userId.equals(timeSlot.getOwnerId())) {
            throw new TimeSlotNotFoundException(
                    "Time slot not found with id: " + command.timeSlotId());
        }
        Calendar calendar = Calendar.createWithSlots(userId, List.of(timeSlot));
        calendar.deleteTimeSlot(command.timeSlotId());
        deleteTimeSlotPort.deleteTimeSlot(command.timeSlotId());
        Publisher.INSTANCE.notifyObservers(
                new TimeSlotDeletedEvent(command.timeSlotId(), userId, Instant.now()));
    }
}
