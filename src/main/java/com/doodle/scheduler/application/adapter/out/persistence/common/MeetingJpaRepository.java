package com.doodle.scheduler.application.adapter.out.persistence.common;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface MeetingJpaRepository extends CrudRepository<MeetingJpaEntity, UUID> {
}
