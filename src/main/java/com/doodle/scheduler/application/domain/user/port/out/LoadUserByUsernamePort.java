package com.doodle.scheduler.application.domain.user.port.out;

import com.doodle.scheduler.application.domain.user.model.User;

public interface LoadUserByUsernamePort {
    User loadUserByUsername(String username);
}
