package com.doodle.scheduler.application.domain.user.model;

import com.doodle.scheduler.application.domain.common.model.AggregateRoot;
import com.doodle.scheduler.application.domain.user.exception.InvalidUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User - Aggregate Root")
class UserTest {

    @Nested
    @DisplayName("User Creation")
    class CreationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "john.doe",
                "A",
                "user.name-123_test@domain",
                "123456",
                "用户名_用户",
                "  john.doe  "
        })
        @DisplayName("Should create User with valid username")
        void shouldCreateUserWithValidUsername(String username) {
            User user = User.create(username);

            assertNotNull(user);
            assertNotNull(user.getId());
        }

        @Test
        @DisplayName("Should accept very long username")
        void shouldAcceptVeryLongUsername() {
            String longUsername = "a".repeat(500);
            User user = User.create(longUsername);

            assertNotNull(user);
            assertNotNull(user.getId());
        }

        @Test
        @DisplayName("Should generate UUID for each user")
        void shouldGenerateUuidForEachUser() {
            User user1 = User.create("alice");
            User user2 = User.create("bob");

            assertNotEquals(user1.getId(), user2.getId());
        }

        @Test
        @DisplayName("Should reject null username")
        void shouldRejectNullUsername() {
            assertThrows(NullPointerException.class, () -> User.create(null));
        }

        @Test
        @DisplayName("Should reject empty username")
        void shouldRejectEmptyUsername() {
            assertThrows(InvalidUsernameException.class, () -> User.create(""));
        }

        @Test
        @DisplayName("Should reject blank username")
        void shouldRejectBlankUsername() {
            assertThrows(InvalidUsernameException.class, () -> User.create("   "));
        }
    }

    @Nested
    @DisplayName("User Accessors")
    class AccessorTests {

        @Test
        @DisplayName("Should return correct id")
        void shouldReturnCorrectId() {
            User user = User.create("testuser");

            assertNotNull(user.getId());
            assertFalse(user.getId().toString().isEmpty());
        }

        @Test
        @DisplayName("Should maintain ID consistency")
        void shouldMaintainIdConsistency() {
            User user = User.create("testuser");
            UUID id1 = user.getId();
            UUID id2 = user.getId();

            assertEquals(id1, id2);
        }
    }

    @Nested
    @DisplayName("User Validation")
    class ValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should reject whitespace-only usernames")
        void shouldRejectWhitespaceOnlyUsernames(String username) {
            assertThrows(InvalidUsernameException.class, () -> User.create(username));
        }
    }

    @Nested
    @DisplayName("User Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should have unique ID for each user")
        void shouldHaveUniqueIdForEachUser() {
            User user1 = User.create("alice");
            User user2 = User.create("alice");

            assertNotEquals(user1.getId(), user2.getId());
        }
    }

    @Nested
    @DisplayName("User as Aggregate Root")
    class AggregateRootTests {

        @Test
        @DisplayName("Should have aggregate root identity")
        void shouldHaveAggregateRootIdentity() {
            User user = User.create("testuser");

            assertNotNull(user.getId());
            assertTrue(user.getId() instanceof UUID);
        }

        @Test
        @DisplayName("Should be an Aggregate Root")
        void shouldBeAnAggregateRoot() {
            User user = User.create("testuser");

            assertTrue(user instanceof AggregateRoot);
        }
    }
}
