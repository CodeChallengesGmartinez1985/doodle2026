package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapper;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaRepository;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.searchtimeslots.LoadTimeSlotByIdPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoadTimeSlotByIdRepositoryAdapter implements LoadTimeSlotByIdPort {

    private final TimeSlotJpaRepository timeSlotJpaRepository;
    private final TimeSlotJpaMapper timeSlotJpaMapper;

    @Override
    public Optional<TimeSlot> loadTimeSlotById(UUID timeSlotId) {
        return timeSlotJpaRepository.findById(timeSlotId)
                .map(timeSlotJpaMapper::toDomain);
    }
}
