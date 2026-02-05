package com.doodle.scheduler.application.domain.calendar.model.timeslot;

import com.doodle.scheduler.application.domain.calendar.exception.InvalidTimeRangeException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeRangeInvalidDurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimeRange - Value Object")
class TimeRangeTest {

    @Nested
    @DisplayName("TimeRange Creation via Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("Should create TimeRange with valid start and end times")
        void shouldCreateTimeRangeWithValidTimes() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            Instant end = Instant.parse("2026-02-05T11:00:00Z");

            TimeRange range = new TimeRange(start, end);

            assertNotNull(range);
            assertEquals(start, range.start());
            assertEquals(end, range.end());
        }

        @Test
        @DisplayName("Should reject null start time")
        void shouldRejectNullStart() {
            Instant end = Instant.parse("2026-02-05T11:00:00Z");

            assertThrows(NullPointerException.class, () -> new TimeRange(null, end));
        }

        @Test
        @DisplayName("Should reject null end time")
        void shouldRejectNullEnd() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(NullPointerException.class, () -> new TimeRange(start, null));
        }

        @Test
        @DisplayName("Should reject end time equal to start time")
        void shouldRejectEndEqualsStart() {
            Instant time = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(InvalidTimeRangeException.class, () -> new TimeRange(time, time));
        }

        @Test
        @DisplayName("Should reject end time before start time")
        void shouldRejectEndBeforeStart() {
            Instant start = Instant.parse("2026-02-05T11:00:00Z");
            Instant end = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(InvalidTimeRangeException.class, () -> new TimeRange(start, end));
        }
    }

    @Nested
    @DisplayName("TimeRange Creation via Factory Method")
    class FactoryMethodTests {

        @Test
        @DisplayName("Should create TimeRange with valid start and duration")
        void shouldCreateTimeRangeWithValidDuration() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            int durationMinutes = 60;

            TimeRange range = TimeRange.of(start, durationMinutes);

            assertNotNull(range);
            assertEquals(start, range.start());
            assertEquals(start.plus(Duration.ofMinutes(60)), range.end());
        }

        @Test
        @DisplayName("Should reject null start in factory method")
        void shouldRejectNullStartInFactory() {
            assertThrows(NullPointerException.class, () -> TimeRange.of(null, 60));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -60, -1000})
        @DisplayName("Should reject non-positive duration")
        void shouldRejectNonPositiveDuration(int duration) {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            assertThrows(TimeRangeInvalidDurationException.class, () -> TimeRange.of(start, duration));
        }

        @Test
        @DisplayName("Should create TimeRange with 1 minute duration")
        void shouldCreateTimeRangeWithOneMinuteDuration() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            TimeRange range = TimeRange.of(start, 1);

            assertEquals(start.plusSeconds(60), range.end());
        }

        @Test
        @DisplayName("Should create TimeRange with large duration")
        void shouldCreateTimeRangeWithLargeDuration() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            int durationMinutes = 1440;

            TimeRange range = TimeRange.of(start, durationMinutes);

            assertEquals(start.plus(Duration.ofMinutes(1440)), range.end());
        }
    }

    @Nested
    @DisplayName("TimeRange Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should have value-based equality")
        void shouldHaveValueBasedEquality() {
            TimeRange range1 = TimeRange.of(Instant.parse("2026-02-05T10:00:00Z"), 60);
            TimeRange range2 = TimeRange.of(Instant.parse("2026-02-05T10:00:00Z"), 60);

            assertEquals(range1, range2);
            assertEquals(range1.hashCode(), range2.hashCode());
        }
    }

    @Nested
    @DisplayName("TimeRange Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very close timestamps")
        void shouldHandleVeryCloseTimestamps() {
            Instant start = Instant.parse("2026-02-05T10:00:00.000Z");
            Instant end = start.plusNanos(1);

            TimeRange range = new TimeRange(start, end);

            assertEquals(start, range.start());
            assertEquals(end, range.end());
        }

        @Test
        @DisplayName("Should handle minimum duration (1 minute)")
        void shouldHandleMinimumDuration() {
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            TimeRange range = TimeRange.of(start, 1);

            assertEquals(start.plus(Duration.ofMinutes(1)), range.end());
        }
    }
}
