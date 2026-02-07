package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapper;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaRepository;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.port.out.timeslot.LoadTimeSlotsByUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoadTimeSlotsByUserRepositoryAdapter implements LoadTimeSlotsByUserPort {

    private final TimeSlotJpaRepository timeSlotJpaRepository;
    private final TimeSlotJpaMapper timeSlotJpaMapper;

    @Override
    public List<TimeSlot> loadTimeSlotsByUserId(UUID userId) {
        return timeSlotJpaRepository.findByOwnerId(userId).stream()
                .map(timeSlotJpaMapper::toDomain)
                .toList();
    }
}
