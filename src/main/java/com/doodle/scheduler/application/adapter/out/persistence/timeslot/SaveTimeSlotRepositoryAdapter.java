package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapper;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaRepository;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.createtimeslot.SaveTimeSlotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveTimeSlotRepositoryAdapter implements SaveTimeSlotPort {

    private final TimeSlotJpaRepository timeSlotJpaRepository;
    private final TimeSlotJpaMapper timeSlotJpaMapper;

    @Override
    public TimeSlot saveTimeSlot(TimeSlot timeSlot) {
        var jpaEntity = timeSlotJpaMapper.toJpaEntity(timeSlot);
        var saved = timeSlotJpaRepository.save(jpaEntity);
        return timeSlotJpaMapper.toDomain(saved);
    }
}
