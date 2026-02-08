package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.BaseJpaSliceTest;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapperImpl;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import({LoadTimeSlotsByUserRepositoryAdapter.class, TimeSlotJpaMapperImpl.class})
@DisplayName("LoadTimeSlotsByUserRepositoryAdapter - Slice Test")
class LoadTimeSlotsByUserRepositoryAdapterSliceTest extends BaseJpaSliceTest {

    @Autowired
    private LoadTimeSlotsByUserRepositoryAdapter loadAdapter;

    private static final UUID TEST_USER_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private static final UUID ANOTHER_USER_ID = UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22");

    @Test
    @DisplayName("GIVEN user with multiple time slots WHEN loadTimeSlotsByUserId THEN returns all user's time slots")
    @Sql(scripts = "/sql/timeslot/seed-user-with-multiple-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldLoadAllTimeSlotsForUser() {
        // WHEN
        List<TimeSlot> timeSlots = loadAdapter.loadTimeSlotsByUserId(TEST_USER_ID);

        // THEN
        assertNotNull(timeSlots, "Time slots list should not be null");
        assertEquals(3, timeSlots.size(), "Should return 3 time slots for the user");

        // Verify all time slots belong to the correct user
        timeSlots.forEach(slot ->
            assertEquals(TEST_USER_ID, slot.getOwnerId(), "All slots should belong to the test user")
        );

        // Verify time slots are properly mapped
        assertTrue(timeSlots.stream().anyMatch(slot ->
            slot.getRange().start().toString().equals("2026-02-08T10:00:00Z")),
            "Should contain first time slot"
        );
        assertTrue(timeSlots.stream().anyMatch(slot ->
            slot.getRange().start().toString().equals("2026-02-08T14:00:00Z")),
            "Should contain second time slot"
        );
        assertTrue(timeSlots.stream().anyMatch(slot ->
            slot.getRange().start().toString().equals("2026-02-09T09:00:00Z")),
            "Should contain third time slot"
        );
    }

    @Test
    @DisplayName("GIVEN user with no time slots WHEN loadTimeSlotsByUserId THEN returns empty list")
    @Sql(scripts = "/sql/timeslot/seed-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnEmptyListWhenUserHasNoTimeSlots() {
        // WHEN
        List<TimeSlot> timeSlots = loadAdapter.loadTimeSlotsByUserId(TEST_USER_ID);

        // THEN
        assertNotNull(timeSlots, "Time slots list should not be null");
        assertTrue(timeSlots.isEmpty(), "Should return empty list when user has no time slots");
    }

    @Test
    @DisplayName("GIVEN non-existent user WHEN loadTimeSlotsByUserId THEN returns empty list")
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnEmptyListForNonExistentUser() {
        // GIVEN
        UUID nonExistentUserId = UUID.fromString("99999999-9999-9999-9999-999999999999");

        // WHEN
        List<TimeSlot> timeSlots = loadAdapter.loadTimeSlotsByUserId(nonExistentUserId);

        // THEN
        assertNotNull(timeSlots, "Time slots list should not be null");
        assertTrue(timeSlots.isEmpty(), "Should return empty list for non-existent user");
    }

    @Test
    @DisplayName("GIVEN multiple users with time slots WHEN loadTimeSlotsByUserId THEN returns only specified user's time slots")
    @Sql(scripts = "/sql/timeslot/seed-multiple-users-with-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldReturnOnlySpecifiedUserTimeSlots() {
        // WHEN
        List<TimeSlot> user1TimeSlots = loadAdapter.loadTimeSlotsByUserId(TEST_USER_ID);
        List<TimeSlot> user2TimeSlots = loadAdapter.loadTimeSlotsByUserId(ANOTHER_USER_ID);

        // THEN
        assertNotNull(user1TimeSlots, "User 1 time slots should not be null");
        assertNotNull(user2TimeSlots, "User 2 time slots should not be null");

        assertEquals(2, user1TimeSlots.size(), "User 1 should have 2 time slots");
        assertEquals(1, user2TimeSlots.size(), "User 2 should have 1 time slot");

        // Verify isolation - each user gets only their own slots
        user1TimeSlots.forEach(slot ->
            assertEquals(TEST_USER_ID, slot.getOwnerId(), "All slots should belong to user 1")
        );
        user2TimeSlots.forEach(slot ->
            assertEquals(ANOTHER_USER_ID, slot.getOwnerId(), "All slots should belong to user 2")
        );
    }
}
