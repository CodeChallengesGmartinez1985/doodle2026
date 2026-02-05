package com.doodle.scheduler.application.domain.meeting.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingDescription - Value Object")
class MeetingDescriptionTest {

    @Nested
    @DisplayName("MeetingDescription Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create MeetingDescription with valid string")
        void shouldCreateMeetingDescriptionWithValidString() {
            MeetingDescription description = new MeetingDescription("Discuss Q1 roadmap");

            assertNotNull(description);
            assertEquals("Discuss Q1 roadmap", description.value());
        }

        @Test
        @DisplayName("Should create MeetingDescription with empty string")
        void shouldCreateMeetingDescriptionWithEmptyString() {
            MeetingDescription description = new MeetingDescription("");

            assertEquals("", description.value());
        }

        @Test
        @DisplayName("Should create MeetingDescription with whitespace-only string")
        void shouldCreateMeetingDescriptionWithWhitespaceOnlyString() {
            MeetingDescription description = new MeetingDescription("   ");

            assertEquals("   ", description.value());
        }

        @Test
        @DisplayName("Should reject null value")
        void shouldRejectNullValue() {
            assertThrows(NullPointerException.class, () -> new MeetingDescription(null));
        }

        @Test
        @DisplayName("Should accept very long description")
        void shouldAcceptVeryLongDescription() {
            String longDescription = "A".repeat(10000);
            MeetingDescription description = new MeetingDescription(longDescription);

            assertEquals(longDescription, description.value());
        }
    }

    @Nested
    @DisplayName("MeetingDescription Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should not perform trimming (unlike MeetingTitle)")
        void shouldNotPerformTrimming() {
            MeetingDescription description = new MeetingDescription("  Description  ");

            assertEquals("  Description  ", description.value());
        }

        @Test
        @DisplayName("Should accept single character")
        void shouldAcceptSingleCharacter() {
            MeetingDescription description = new MeetingDescription("X");

            assertEquals("X", description.value());
        }
    }

    @Nested
    @DisplayName("MeetingDescription Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should have value-based equality")
        void shouldHaveValueBasedEquality() {
            MeetingDescription desc1 = new MeetingDescription("Q1 Planning");
            MeetingDescription desc2 = new MeetingDescription("Q1 Planning");

            assertEquals(desc1, desc2);
        }

        @Test
        @DisplayName("Should have consistent hash code for equal values")
        void shouldHaveConsistentHashCodeForEqualValues() {
            MeetingDescription desc1 = new MeetingDescription("Q1 Planning");
            MeetingDescription desc2 = new MeetingDescription("Q1 Planning");

            assertEquals(desc1.hashCode(), desc2.hashCode());
        }

        @Test
        @DisplayName("Should differentiate empty from whitespace")
        void shouldDifferentiateEmptyFromWhitespace() {
            MeetingDescription empty = new MeetingDescription("");
            MeetingDescription whitespace = new MeetingDescription("   ");

            assertNotEquals(empty, whitespace);
        }
    }
}
