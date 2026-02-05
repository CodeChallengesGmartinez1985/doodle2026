package com.doodle.scheduler.application.domain.user.exception;

import com.doodle.scheduler.application.domain.common.exception.DomainException;

public class InvalidUsernameException extends DomainException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
