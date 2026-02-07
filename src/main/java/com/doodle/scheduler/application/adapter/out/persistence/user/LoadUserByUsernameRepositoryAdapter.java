package com.doodle.scheduler.application.adapter.out.persistence.user;

import com.doodle.scheduler.application.adapter.out.persistence.user.common.UserJpaMapper;
import com.doodle.scheduler.application.adapter.out.persistence.user.common.UserJpaRepository;
import com.doodle.scheduler.application.domain.user.exception.UserNotFoundException;
import com.doodle.scheduler.application.domain.user.model.User;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadUserByUsernameRepositoryAdapter implements LoadUserByUsernamePort {

    private final UserJpaRepository userJpaRepository;
    private final UserJpaMapper userJpaMapper;

    @Override
    public User loadUserByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userJpaMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }
}
