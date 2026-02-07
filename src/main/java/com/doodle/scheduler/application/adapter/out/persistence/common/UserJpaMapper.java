package com.doodle.scheduler.application.adapter.out.persistence.common;

import com.doodle.scheduler.application.domain.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserJpaMapper {

    default User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;
        return User.create(entity.getUsername());
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserJpaEntity toJpaEntity(User user);
}
