package com.doodle.scheduler.application.adapter.out.persistence.user.common;

import com.doodle.scheduler.application.domain.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    default User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.reconstitute(entity.getId(), entity.getUsername());
    }

    UserJpaEntity toJpaEntity(User user);
}
