package com.doodle.scheduler.application.adapter.out.persistence.common;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface UserJpaRepository extends CrudRepository<UserJpaEntity, UUID> {
}
