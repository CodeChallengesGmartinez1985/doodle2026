package com.doodle.scheduler.application.domain.calendar.service;

import com.doodle.scheduler.application.domain.calendar.model.Calendar;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.in.createtimeslot.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.port.in.createtimeslot.CreateTimeSlotUseCase;
import com.doodle.scheduler.application.domain.calendar.port.out.LoadTimeSlotsByUserPort;
import com.doodle.scheduler.application.domain.calendar.port.out.SaveTimeSlotPort;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;
import com.doodle.scheduler.application.domain.user.model.User;
import com.doodle.scheduler.application.domain.common.events.Publisher;
import com.doodle.scheduler.application.domain.common.events.TimeSlotCreatedEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CreateTimeSlotServiceImpl implements CreateTimeSlotUseCase {

    private final LoadUserByUsernamePort loadUserByUsernamePort;
    private final LoadTimeSlotsByUserPort loadTimeSlotsByUserPort;
    private final SaveTimeSlotPort saveTimeSlotPort;

    public CreateTimeSlotServiceImpl(LoadUserByUsernamePort loadUserByUsernamePort, LoadTimeSlotsByUserPort loadTimeSlotsByUserPort, SaveTimeSlotPort saveTimeSlotPort) {
        this.loadUserByUsernamePort = loadUserByUsernamePort;
        this.loadTimeSlotsByUserPort = loadTimeSlotsByUserPort;
        this.saveTimeSlotPort = saveTimeSlotPort;
    }

    @Override
    public TimeSlot execute(CreateTimeSlotCommand command) {
        User user = loadUserByUsernamePort.loadUserByUsername(command.username());
        UUID userId = user.getId();
        List<TimeSlot> existingSlots = loadTimeSlotsByUserPort.loadTimeSlotsByUserId(userId);
        Calendar calendar = Calendar.createWithSlots(userId, existingSlots);
        TimeSlot newSlot = calendar.addTimeSlot(command.start(), command.durationMinutes());
        TimeSlot savedSlot = saveTimeSlotPort.saveTimeSlot(newSlot);
        Publisher.INSTANCE.notifyObservers(new TimeSlotCreatedEvent(savedSlot, Instant.now()));
        return savedSlot;
    }
}
