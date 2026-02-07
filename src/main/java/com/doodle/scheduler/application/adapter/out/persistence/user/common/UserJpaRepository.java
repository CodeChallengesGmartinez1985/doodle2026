package com.doodle.scheduler.application.adapter.out.persistence.user.common;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends CrudRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByUsername(String username);
}
