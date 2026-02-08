package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaRepository;
import com.doodle.scheduler.application.domain.calendar.port.out.DeleteTimeSlotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteTimeSlotRepositoryAdapter implements DeleteTimeSlotPort {

    private final TimeSlotJpaRepository timeSlotJpaRepository;

    @Override
    public void deleteTimeSlot(UUID timeSlotId) {
        timeSlotJpaRepository.deleteById(timeSlotId);
    }
}
