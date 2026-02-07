package com.doodle.scheduler.application.domain.user.model;

import com.doodle.scheduler.application.domain.common.model.AggregateRoot;
import com.doodle.scheduler.application.domain.user.exception.InvalidUsernameException;

import java.util.Objects;
import java.util.UUID;

public class User extends AggregateRoot {
    /**
     * Attributes
     */
    private final String username;

    /**
     * Public API
     */
    public static User create(String username) {
        String validUsername = validateUsername(username);
        return new User(UUID.randomUUID(), validUsername);
    }

    public static User reconstitute(UUID id, String username) {
        Objects.requireNonNull(id, "id must not be null");
        String validUsername = validateUsername(username);
        return new User(id, validUsername);
    }

    public String getUsername() {
        return username;
    }

    /**
     * Private methods / constructors
     */
    private static String validateUsername(String username) {
        Objects.requireNonNull(username, "username must not be null");
        String u = username.trim();
        if (u.isEmpty()) throw new InvalidUsernameException("username must not be blank");
        return u;
    }

    private User(UUID id, String username) {
        super(id);
        this.username = username;
    }
}
