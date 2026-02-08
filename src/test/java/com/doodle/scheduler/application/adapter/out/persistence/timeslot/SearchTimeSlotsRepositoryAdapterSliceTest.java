package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.BaseJpaSliceTest;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapperImpl;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.SearchTimeSlotsPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import({SearchTimeSlotsRepositoryAdapter.class, TimeSlotJpaMapperImpl.class})
@DisplayName("SearchTimeSlotsRepositoryAdapter - Slice Test")
class SearchTimeSlotsRepositoryAdapterSliceTest extends BaseJpaSliceTest {

    @Autowired
    private SearchTimeSlotsRepositoryAdapter searchAdapter;

    private static final UUID TEST_USER_ID = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
    private static final UUID ANOTHER_USER_ID = UUID.fromString("b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22");

    @Nested
    @DisplayName("Basic Search Scenarios")
    class BasicSearchScenarios {

        @Test
        @DisplayName("GIVEN user with multiple time slots WHEN search with only ownerId THEN returns all user's time slots ordered by start time")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnAllTimeSlotsForOwner() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, Integer.MAX_VALUE
            );

            // THEN
            assertNotNull(result, "Search result should not be null");
            assertNotNull(result.timeSlots(), "Time slots list should not be null");
            assertEquals(12, result.totalElements(), "Should have 12 total time slots");
            assertEquals(12, result.timeSlots().size(), "Should return all 12 time slots");

            // Verify all belong to correct owner
            assertAllSlotsHaveOwnerId(result, TEST_USER_ID, "All slots should belong to test user");

            // Verify ordering by start time (ascending)
            List<Instant> startTimes = result.timeSlots().stream()
                .map(slot -> slot.getRange().start())
                .toList();

            for (int i = 1; i < startTimes.size(); i++) {
                assertTrue(
                    !startTimes.get(i).isBefore(startTimes.get(i - 1)),
                    "Time slots should be ordered by start time ascending"
                );
            }
        }

        @Test
        @DisplayName("GIVEN user with no time slots WHEN search THEN returns empty result")
        @Sql(scripts = "/sql/timeslot/seed-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnEmptyResultWhenNoTimeSlots() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 10
            );

            // THEN
            assertNotNull(result, "Search result should not be null");
            assertNotNull(result.timeSlots(), "Time slots list should not be null");
            assertTrue(result.timeSlots().isEmpty(), "Should return empty list");
            assertEquals(0, result.totalElements(), "Total elements should be 0");
        }

        @Test
        @DisplayName("GIVEN null ownerId WHEN search THEN throws IllegalArgumentException")
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldThrowExceptionWhenOwnerIdIsNull() {
            // WHEN & THEN
            assertThrows(IllegalArgumentException.class, () ->
                searchAdapter.searchTimeSlots(null, null, null, null, 0, 10),
                "Should throw IllegalArgumentException when ownerId is null"
            );
        }
    }

    @Nested
    @DisplayName("Status Filter Scenarios")
    class StatusFilterScenarios {

        @ParameterizedTest(name = "GIVEN time slots with different states WHEN search with {0} status THEN returns only {0} slots")
        @CsvSource({
            "AVAILABLE, 10, All slots should be AVAILABLE",
            "BUSY, 2, All slots should be BUSY"
        })
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldFilterByStatus(String status, int expectedCount, String assertionMessage) {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, status, null, null, 0, Integer.MAX_VALUE
            );

            // THEN
            assertNotNull(result, "Search result should not be null");
            assertEquals(expectedCount, result.totalElements(),
                String.format("Should have %d %s time slots", expectedCount, status));
            assertEquals(expectedCount, result.timeSlots().size(),
                String.format("Should return %d %s time slots", expectedCount, status));

            // Verify all have the expected state
            assertAllSlotsHaveState(result, status, assertionMessage);
        }

        @Test
        @DisplayName("GIVEN time slots WHEN search with empty status string THEN returns all time slots")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnAllWhenStatusIsEmpty() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, "", null, null, 0, Integer.MAX_VALUE
            );

            // THEN
            assertEquals(12, result.totalElements(), "Should return all time slots when status is empty");
        }
    }

    @Nested
    @DisplayName("Time Range Filter Scenarios")
    class TimeRangeFilterScenarios {

        @Test
        @DisplayName("GIVEN time slots WHEN search with startTime filter THEN returns slots starting at or after startTime")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldFilterByStartTime() {
            // GIVEN - filter from Feb 12, 2026 onwards
            Instant startTime = Instant.parse("2026-02-12T00:00:00Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, startTime, null, 0, Integer.MAX_VALUE
            );

            // THEN
            assertEquals(4, result.totalElements(), "Should have 4 time slots from Feb 12 onwards");

            // Verify all start times are >= startTime
            assertAllSlotsStartAtOrAfter(result, startTime);
        }

        @Test
        @DisplayName("GIVEN time slots WHEN search with endTime filter THEN returns slots ending at or before endTime")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldFilterByEndTime() {
            // GIVEN - filter up to Feb 9, 2026 end of day
            Instant endTime = Instant.parse("2026-02-09T23:59:59Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, endTime, 0, Integer.MAX_VALUE
            );

            // THEN
            assertEquals(7, result.totalElements(), "Should have 7 time slots up to Feb 9");

            // Verify all end times are <= endTime
            assertAllSlotsEndAtOrBefore(result, endTime);
        }

        @Test
        @DisplayName("GIVEN time slots WHEN search with both startTime and endTime THEN returns slots within date range")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldFilterByDateRange() {
            // GIVEN - filter for Feb 8-9, 2026
            Instant startTime = Instant.parse("2026-02-08T00:00:00Z");
            Instant endTime = Instant.parse("2026-02-09T23:59:59Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, startTime, endTime, 0, Integer.MAX_VALUE
            );

            // THEN
            assertEquals(7, result.totalElements(), "Should have 7 time slots in Feb 8-9 range");

            // Verify all slots are within range
            assertAllSlotsWithinDateRange(result, startTime, endTime);
        }

        @Test
        @DisplayName("GIVEN time slots at exact boundary WHEN search with exact startTime THEN includes boundary slot")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldIncludeBoundarySlots() {
            // GIVEN - exact start time of a slot
            Instant exactStartTime = Instant.parse("2026-02-10T08:00:00Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, exactStartTime, null, 0, Integer.MAX_VALUE
            );

            // THEN
            assertTrue(result.totalElements() >= 1, "Should include slot at exact boundary");
            assertTrue(
                result.timeSlots().stream()
                    .anyMatch(slot -> slot.getRange().start().equals(exactStartTime)),
                "Should include slot starting at exact boundary time"
            );
        }
    }

    @Nested
    @DisplayName("Pagination Scenarios")
    class PaginationScenarios {

        @Test
        @DisplayName("GIVEN 12 time slots WHEN request first page with size 5 THEN returns 5 slots")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnFirstPage() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 5
            );

            // THEN
            assertEquals(12, result.totalElements(), "Total elements should be 12");
            assertEquals(5, result.timeSlots().size(), "Should return 5 slots on first page");
        }

        @Test
        @DisplayName("GIVEN 12 time slots WHEN request second page with size 5 THEN returns next 5 slots")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnSecondPage() {
            // WHEN
            SearchTimeSlotsPort.SearchResult page1 = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 5
            );
            SearchTimeSlotsPort.SearchResult page2 = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 1, 5
            );

            // THEN
            assertEquals(12, page1.totalElements(), "Total elements should be 12");
            assertEquals(12, page2.totalElements(), "Total elements should be consistent across pages");
            assertEquals(5, page1.timeSlots().size(), "First page should have 5 slots");
            assertEquals(5, page2.timeSlots().size(), "Second page should have 5 slots");

            // Verify no overlap between pages
            List<UUID> page1Ids = page1.timeSlots().stream()
                .map(TimeSlot::getId)
                .toList();
            List<UUID> page2Ids = page2.timeSlots().stream()
                .map(TimeSlot::getId)
                .toList();

            assertTrue(
                page1Ids.stream().noneMatch(page2Ids::contains),
                "Pages should not contain overlapping slots"
            );
        }

        @Test
        @DisplayName("GIVEN 12 time slots WHEN request third page with size 5 THEN returns remaining 2 slots")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnLastPageWithRemainingSlots() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 2, 5
            );

            // THEN
            assertEquals(12, result.totalElements(), "Total elements should be 12");
            assertEquals(2, result.timeSlots().size(), "Last page should have 2 remaining slots");
        }

        @Test
        @DisplayName("GIVEN 12 time slots WHEN request page beyond available data THEN returns empty result")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnEmptyWhenPageBeyondData() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 10, 5
            );

            // THEN
            assertEquals(12, result.totalElements(), "Total elements should still be 12");
            assertTrue(result.timeSlots().isEmpty(), "Should return empty list for out of range page");
        }

        @Test
        @DisplayName("GIVEN time slots WHEN request different page sizes THEN pagination works correctly")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldHandleDifferentPageSizes() {
            // WHEN - page size 3
            SearchTimeSlotsPort.SearchResult result3 = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 3
            );

            // WHEN - page size 10
            SearchTimeSlotsPort.SearchResult result10 = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 10
            );

            // THEN
            assertEquals(3, result3.timeSlots().size(), "Should return 3 slots with page size 3");
            assertEquals(10, result10.timeSlots().size(), "Should return 10 slots with page size 10");
            assertEquals(12, result3.totalElements(), "Total elements should be consistent");
            assertEquals(12, result10.totalElements(), "Total elements should be consistent");
        }
    }

    @Nested
    @DisplayName("Combined Filter Scenarios")
    class CombinedFilterScenarios {

        @Test
        @DisplayName("GIVEN time slots WHEN search with status + date range + pagination THEN applies all filters correctly")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldApplyAllFiltersTogether() {
            // GIVEN - filter for AVAILABLE slots in Feb 8-9, first page size 3
            Instant startTime = Instant.parse("2026-02-08T00:00:00Z");
            Instant endTime = Instant.parse("2026-02-09T23:59:59Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, "AVAILABLE", startTime, endTime, 0, 3
            );

            // THEN
            assertEquals(5, result.totalElements(), "Should have 5 available slots in Feb 8-9");
            assertEquals(3, result.timeSlots().size(), "Should return 3 slots on first page");

            // Verify all constraints
            assertAllSlotsHaveState(result, "AVAILABLE", "Should be AVAILABLE");
            assertAllSlotsHaveOwnerId(result, TEST_USER_ID, "Should belong to test user");
            assertAllSlotsWithinDateRange(result, startTime, endTime);
        }

        @Test
        @DisplayName("GIVEN time slots WHEN search BUSY slots in specific date range THEN returns correct filtered result")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldFilterBusySlotsInDateRange() {
            // GIVEN - BUSY slots in Feb 8-9
            Instant startTime = Instant.parse("2026-02-08T00:00:00Z");
            Instant endTime = Instant.parse("2026-02-09T23:59:59Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, "BUSY", startTime, endTime, 0, 10
            );

            // THEN
            assertEquals(2, result.totalElements(), "Should have 2 busy slots in Feb 8-9");
            assertEquals(2, result.timeSlots().size(), "Should return 2 busy slots");

            assertAllSlotsHaveState(result, "BUSY", "All should be BUSY");
        }

        @Test
        @DisplayName("GIVEN time slots WHEN search with filters that match nothing THEN returns empty result")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldReturnEmptyWhenNoMatch() {
            // GIVEN - search for BUSY slots in future date with no data
            Instant startTime = Instant.parse("2026-03-01T00:00:00Z");
            Instant endTime = Instant.parse("2026-03-31T23:59:59Z");

            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, "BUSY", startTime, endTime, 0, 10
            );

            // THEN
            assertEquals(0, result.totalElements(), "Should have no matching slots");
            assertTrue(result.timeSlots().isEmpty(), "Should return empty list");
        }
    }

    @Nested
    @DisplayName("Multiple Users Isolation Scenarios")
    class MultipleUsersIsolationScenarios {

        @Test
        @DisplayName("GIVEN multiple users with time slots WHEN search by ownerId THEN returns only that user's slots")
        @Sql(scripts = "/sql/timeslot/seed-multiple-users-with-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldIsolateUserTimeSlots() {
            // WHEN
            SearchTimeSlotsPort.SearchResult user1Result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 10
            );
            SearchTimeSlotsPort.SearchResult user2Result = searchAdapter.searchTimeSlots(
                ANOTHER_USER_ID, null, null, null, 0, 10
            );

            // THEN
            assertEquals(2, user1Result.totalElements(), "User 1 should have 2 slots");
            assertEquals(1, user2Result.totalElements(), "User 2 should have 1 slot");

            // Verify isolation
            assertAllSlotsHaveOwnerId(user1Result, TEST_USER_ID, "Should belong to user 1");
            assertAllSlotsHaveOwnerId(user2Result, ANOTHER_USER_ID, "Should belong to user 2");
        }
    }

    @Nested
    @DisplayName("Domain Mapping Verification Scenarios")
    class DomainMappingVerificationScenarios {

        @Test
        @DisplayName("GIVEN time slots WHEN search THEN all domain fields are correctly mapped")
        @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        void shouldMapAllDomainFieldsCorrectly() {
            // WHEN
            SearchTimeSlotsPort.SearchResult result = searchAdapter.searchTimeSlots(
                TEST_USER_ID, null, null, null, 0, 1
            );

            // THEN
            assertFalse(result.timeSlots().isEmpty(), "Should have at least one slot");

            TimeSlot slot = result.timeSlots().get(0);

            assertNotNull(slot.getId(), "ID should be mapped");
            assertNotNull(slot.getOwnerId(), "Owner ID should be mapped");
            assertNotNull(slot.getRange(), "Range should be mapped");
            assertNotNull(slot.getRange().start(), "Start time should be mapped");
            assertNotNull(slot.getRange().end(), "End time should be mapped");
            assertNotNull(slot.getStateString(), "State should be mapped");
            assertTrue(slot.getDurationMinutes() > 0, "Duration should be positive");

            // Verify calculated fields
            long expectedDuration = (slot.getRange().end().toEpochMilli() - slot.getRange().start().toEpochMilli()) / 60_000;
            assertEquals(expectedDuration, slot.getDurationMinutes(), "Duration should be calculated correctly");
        }
    }

    /**
     * Helper methods to encapsulate common verification patterns
     */
    private void assertAllSlotsHaveOwnerId(SearchTimeSlotsPort.SearchResult result, UUID expectedOwnerId, String message) {
        result.timeSlots().forEach(slot ->
            assertEquals(expectedOwnerId, slot.getOwnerId(), message)
        );
    }

    private void assertAllSlotsHaveState(SearchTimeSlotsPort.SearchResult result, String expectedState, String message) {
        result.timeSlots().forEach(slot ->
            assertEquals(expectedState, slot.getStateString(), message)
        );
    }

    private void assertAllSlotsStartAtOrAfter(SearchTimeSlotsPort.SearchResult result, Instant startTime) {
        result.timeSlots().forEach(slot ->
            assertTrue(
                !slot.getRange().start().isBefore(startTime),
                "All slots should start at or after " + startTime
            )
        );
    }

    private void assertAllSlotsEndAtOrBefore(SearchTimeSlotsPort.SearchResult result, Instant endTime) {
        result.timeSlots().forEach(slot ->
            assertTrue(
                !slot.getRange().end().isAfter(endTime),
                "All slots should end at or before " + endTime
            )
        );
    }

    private void assertAllSlotsWithinDateRange(SearchTimeSlotsPort.SearchResult result, Instant startTime, Instant endTime) {
        result.timeSlots().forEach(slot -> {
            assertTrue(
                !slot.getRange().start().isBefore(startTime),
                "Slot should start at or after " + startTime
            );
            assertTrue(
                !slot.getRange().end().isAfter(endTime),
                "Slot should end at or before " + endTime
            );
        });
    }
}
