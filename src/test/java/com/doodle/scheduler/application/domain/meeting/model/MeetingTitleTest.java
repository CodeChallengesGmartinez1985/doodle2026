package com.doodle.scheduler.application.domain.meeting.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingTitle - Value Object")
class MeetingTitleTest {

    @Nested
    @DisplayName("MeetingTitle Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create MeetingTitle with valid string")
        void shouldCreateMeetingTitleWithValidString() {
            MeetingTitle title = new MeetingTitle("Team Meeting");

            assertNotNull(title);
            assertEquals("Team Meeting", title.value());
        }

        @Test
        @DisplayName("Should reject null value")
        void shouldRejectNullValue() {
            assertThrows(NullPointerException.class, () -> new MeetingTitle(null));
        }

        @Test
        @DisplayName("Should reject blank string")
        void shouldRejectBlankString() {
            assertThrows(IllegalArgumentException.class, () -> new MeetingTitle("   "));
        }

        @Test
        @DisplayName("Should reject empty string")
        void shouldRejectEmptyString() {
            assertThrows(IllegalArgumentException.class, () -> new MeetingTitle(""));
        }

        @Test
        @DisplayName("Should trim whitespace from value")
        void shouldTrimWhitespaceFromValue() {
            MeetingTitle title = new MeetingTitle("  Team Meeting  ");

            assertEquals("Team Meeting", title.value());
        }

        @Test
        @DisplayName("Should accept single character")
        void shouldAcceptSingleCharacter() {
            MeetingTitle title = new MeetingTitle("A");

            assertEquals("A", title.value());
        }

        @Test
        @DisplayName("Should accept title with special characters")
        void shouldAcceptTitleWithSpecialCharacters() {
            MeetingTitle title = new MeetingTitle("Q&A Session (2026)");

            assertEquals("Q&A Session (2026)", title.value());
        }

        @Test
        @DisplayName("Should accept very long title")
        void shouldAcceptVeryLongTitle() {
            String longTitle = "A".repeat(500);
            MeetingTitle title = new MeetingTitle(longTitle);

            assertEquals(longTitle, title.value());
        }
    }

    @Nested
    @DisplayName("MeetingTitle Validation")
    class ValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should reject whitespace-only titles")
        void shouldRejectWhitespaceOnlyTitles(String input) {
            assertThrows(IllegalArgumentException.class, () -> new MeetingTitle(input));
        }
    }

    @Nested
    @DisplayName("MeetingTitle Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should have value-based equality")
        void shouldHaveValueBasedEquality() {
            MeetingTitle title1 = new MeetingTitle("Team Meeting");
            MeetingTitle title2 = new MeetingTitle("Team Meeting");

            assertEquals(title1, title2);
        }

        @Test
        @DisplayName("Should have consistent hash code for equal values")
        void shouldHaveConsistentHashCodeForEqualValues() {
            MeetingTitle title1 = new MeetingTitle("Team Meeting");
            MeetingTitle title2 = new MeetingTitle("Team Meeting");

            assertEquals(title1.hashCode(), title2.hashCode());
        }

        @Test
        @DisplayName("Should not equal titles with different values")
        void shouldNotEqualTitleWithDifferentValues() {
            MeetingTitle title1 = new MeetingTitle("Team Meeting");
            MeetingTitle title2 = new MeetingTitle("Other Meeting");

            assertNotEquals(title1, title2);
        }
    }
}
