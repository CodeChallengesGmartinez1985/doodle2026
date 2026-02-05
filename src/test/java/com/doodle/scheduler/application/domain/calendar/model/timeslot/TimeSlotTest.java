package com.doodle.scheduler.application.domain.calendar.model.timeslot;

import com.doodle.scheduler.application.domain.calendar.exception.InvalidSlotStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimeSlot - Entity")
class TimeSlotTest {

    @Nested
    @DisplayName("TimeSlot Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create TimeSlot with valid parameters")
        void shouldCreateTimeSlotWithValidParameters() {
            UUID id = UUID.randomUUID();
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            int durationMinutes = 60;

            TimeSlot slot = TimeSlot.create(id, start, durationMinutes);

            assertNotNull(slot);
            assertEquals(id, slot.getId());
            assertEquals(start, slot.getRange().start());
            assertNotNull(slot.getState());
        }

        @Test
        @DisplayName("Should create TimeSlot in Available state by default")
        void shouldCreateTimeSlotInAvailableState() {
            UUID id = UUID.randomUUID();
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            TimeSlot slot = TimeSlot.create(id, start, 60);

            assertTrue(slot.getState().isAvailable());
            assertFalse(slot.getState().isBusy());
        }

        @Test
        @DisplayName("Should reject null id")
        void shouldRejectNullId() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(NullPointerException.class, () -> TimeSlot.create(null, start, 60));
        }

        @Test
        @DisplayName("Should reject null start time")
        void shouldRejectNullStartTime() {
            UUID id = UUID.randomUUID();

            assertThrows(NullPointerException.class, () -> TimeSlot.create(id, null, 60));
        }

        @Test
        @DisplayName("Should reject non-positive duration")
        void shouldRejectNonPositiveDuration() {
            UUID id = UUID.randomUUID();
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(Exception.class, () -> TimeSlot.create(id, start, 0));
            assertThrows(Exception.class, () -> TimeSlot.create(id, start, -60));
        }
    }

    @Nested
    @DisplayName("TimeSlot State Transitions")
    class StateTransitionTests {

        @Test
        @DisplayName("Should transition from Available to Busy")
        void shouldTransitionFromAvailableToBusy() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);
            assertTrue(slot.getState().isAvailable());

            slot.markBusy();

            assertTrue(slot.getState().isBusy());
            assertFalse(slot.getState().isAvailable());
        }

        @Test
        @DisplayName("Should transition from Busy to Available")
        void shouldTransitionFromBusyToAvailable() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);
            slot.markBusy();
            assertTrue(slot.getState().isBusy());

            slot.markAvailable();

            assertTrue(slot.getState().isAvailable());
            assertFalse(slot.getState().isBusy());
        }

        @Test
        @DisplayName("Should reject invalid transition - markAvailable on Available state")
        void shouldRejectInvalidTransitionMarkAvailableOnAvailable() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);
            assertTrue(slot.getState().isAvailable());

            assertThrows(InvalidSlotStateTransitionException.class, slot::markAvailable);
        }

        @Test
        @DisplayName("Should reject invalid transition - markBusy on Busy state")
        void shouldRejectInvalidTransitionMarkBusyOnBusy() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);
            slot.markBusy();
            assertTrue(slot.getState().isBusy());

            assertThrows(InvalidSlotStateTransitionException.class, slot::markBusy);
        }

        @Test
        @DisplayName("Should allow multiple state transitions")
        void shouldAllowMultipleStateTransitions() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);

            slot.markBusy();
            assertTrue(slot.getState().isBusy());

            slot.markAvailable();
            assertTrue(slot.getState().isAvailable());

            slot.markBusy();
            assertTrue(slot.getState().isBusy());

            slot.markAvailable();
            assertTrue(slot.getState().isAvailable());
        }
    }

    @Nested
    @DisplayName("TimeSlot Time Range Changes")
    class TimeRangeChangeTests {

        @Test
        @DisplayName("Should update time range successfully")
        void shouldUpdateTimeRangeSuccessfully() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.parse("2026-02-05T10:00:00Z"), 60);
            Instant newStart = Instant.parse("2026-02-05T14:00:00Z");

            slot.changeTimeRange(newStart, 120);

            assertEquals(newStart, slot.getRange().start());
            assertEquals(newStart.plusSeconds(120 * 60L), slot.getRange().end());
        }

        @Test
        @DisplayName("Should update time range while preserving state")
        void shouldUpdateTimeRangePreservingState() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.now(), 60);
            slot.markBusy();
            assertTrue(slot.getState().isBusy());

            slot.changeTimeRange(Instant.now().plusSeconds(7200), 90);

            assertTrue(slot.getState().isBusy());
        }

        @Test
        @DisplayName("Should allow multiple time range changes")
        void shouldAllowMultipleTimeRangeChanges() {
            TimeSlot slot = TimeSlot.create(UUID.randomUUID(), Instant.parse("2026-02-05T10:00:00Z"), 60);

            slot.changeTimeRange(Instant.parse("2026-02-05T11:00:00Z"), 90);
            assertEquals(Instant.parse("2026-02-05T11:00:00Z"), slot.getRange().start());

            slot.changeTimeRange(Instant.parse("2026-02-05T13:00:00Z"), 120);
            assertEquals(Instant.parse("2026-02-05T13:00:00Z"), slot.getRange().start());
        }
    }
}
