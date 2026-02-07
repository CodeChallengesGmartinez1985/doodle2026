package com.doodle.scheduler.application.adapter.out.persistence.timeslot.common;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TimeSlotJpaRepository extends CrudRepository<TimeSlotJpaEntity, UUID> {
    List<TimeSlotJpaEntity> findByOwnerId(UUID ownerId);
}
