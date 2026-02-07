package com.doodle.scheduler.application.domain.user.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
