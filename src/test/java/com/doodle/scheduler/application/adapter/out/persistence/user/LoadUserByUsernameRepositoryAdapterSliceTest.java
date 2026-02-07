package com.doodle.scheduler.application.adapter.out.persistence.user;

import com.doodle.scheduler.application.adapter.out.persistence.BaseJpaSliceTest;
import com.doodle.scheduler.application.adapter.out.persistence.user.common.UserJpaMapperImpl;
import com.doodle.scheduler.application.domain.user.exception.UserNotFoundException;
import com.doodle.scheduler.application.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import({LoadUserByUsernameRepositoryAdapter.class, UserJpaMapperImpl.class})
@DisplayName("LoadUserByUsernameRepositoryAdapter - Slice Test")
class LoadUserByUsernameRepositoryAdapterSliceTest extends BaseJpaSliceTest {

    @Autowired
    private LoadUserByUsernameRepositoryAdapter loadAdapter;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String TEST_USERNAME = "test-user";

    @Test
    @DisplayName("GIVEN existing username WHEN loadUserByUsername THEN returns user with correct mapping")
    @Sql(scripts = "/sql/user/seed-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldLoadUserByUsername() {
        // WHEN
        User user = loadAdapter.loadUserByUsername(TEST_USERNAME);

        // THEN
        assertNotNull(user, "User should not be null");
        assertEquals(TEST_USER_ID, user.getId(), "User ID should match");
        assertEquals(TEST_USERNAME, user.getUsername(), "Username should match");
    }

    @Test
    @DisplayName("GIVEN non-existent username WHEN loadUserByUsername THEN throws UserNotFoundException")
    @Sql(value = "/sql/user/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowUserNotFoundExceptionWhenUsernameDoesNotExist() {
        // GIVEN
        String nonExistentUsername = "non-existent-user";

        // WHEN & THEN
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> loadAdapter.loadUserByUsername(nonExistentUsername),
                "Expected UserNotFoundException when username doesn't exist"
        );

        // Verify exception message
        assertNotNull(exception.getMessage(), "Exception should have a message");
        assertTrue(exception.getMessage().contains(nonExistentUsername),
                "Exception message should contain the username");
    }

    @Test
    @DisplayName("GIVEN multiple users WHEN loadUserByUsername THEN returns only the matching user")
    @Sql(scripts = "/sql/user/seed-multiple-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldLoadCorrectUserWhenMultipleUsersExist() {
        // WHEN
        User user1 = loadAdapter.loadUserByUsername("user-one");
        User user2 = loadAdapter.loadUserByUsername("user-two");

        // THEN
        assertNotNull(user1, "User 1 should not be null");
        assertNotNull(user2, "User 2 should not be null");

        assertEquals("user-one", user1.getUsername(), "User 1 username should match");
        assertEquals("user-two", user2.getUsername(), "User 2 username should match");

        assertNotEquals(user1.getId(), user2.getId(), "Users should have different IDs");
    }

    @Test
    @DisplayName("GIVEN username with different casing WHEN loadUserByUsername THEN searches are case-sensitive")
    @Sql(scripts = "/sql/user/seed-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldBeCaseSensitiveForUsername() {
        // WHEN & THEN
        assertThrows(
                UserNotFoundException.class,
                () -> loadAdapter.loadUserByUsername("TEST-USER"),
                "Should not find user with different casing"
        );

        assertThrows(
                UserNotFoundException.class,
                () -> loadAdapter.loadUserByUsername("Test-User"),
                "Should not find user with different casing"
        );

        // But should find with exact casing
        assertDoesNotThrow(() -> loadAdapter.loadUserByUsername(TEST_USERNAME),
                "Should find user with exact casing");
    }
}
