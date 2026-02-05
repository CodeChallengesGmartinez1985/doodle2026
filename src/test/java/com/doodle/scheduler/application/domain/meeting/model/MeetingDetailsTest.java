package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.meeting.exception.MeetingWithoutParticipantsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingDetails - Value Object")
class MeetingDetailsTest {

    @Nested
    @DisplayName("MeetingDetails Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create MeetingDetails with valid parameters")
        void shouldCreateMeetingDetailsWithValidParameters() {
            MeetingTitle title = new MeetingTitle("Team Meeting");
            MeetingDescription description = new MeetingDescription("Q1 Planning");
            Set<UUID> participants = Set.of(UUID.randomUUID(), UUID.randomUUID());

            MeetingDetails details = new MeetingDetails(title, description, participants);

            assertNotNull(details);
            assertEquals(title, details.meetingTitle());
            assertEquals(description, details.meetingDescription());
            assertEquals(2, details.participants().size());
        }

        @Test
        @DisplayName("Should reject null title")
        void shouldRejectNullTitle() {
            MeetingDescription description = new MeetingDescription("Description");
            Set<UUID> participants = Set.of(UUID.randomUUID());

            assertThrows(NullPointerException.class,
                    () -> new MeetingDetails(null, description, participants));
        }

        @Test
        @DisplayName("Should reject null description")
        void shouldRejectNullDescription() {
            MeetingTitle title = new MeetingTitle("Title");
            Set<UUID> participants = Set.of(UUID.randomUUID());

            assertThrows(NullPointerException.class,
                    () -> new MeetingDetails(title, null, participants));
        }

        @Test
        @DisplayName("Should reject null participants set")
        void shouldRejectNullParticipantsSet() {
            MeetingTitle title = new MeetingTitle("Title");
            MeetingDescription description = new MeetingDescription("Description");

            assertThrows(NullPointerException.class,
                    () -> new MeetingDetails(title, description, null));
        }

        @Test
        @DisplayName("Should reject empty participants set")
        void shouldRejectEmptyParticipantsSet() {
            MeetingTitle title = new MeetingTitle("Title");
            MeetingDescription description = new MeetingDescription("Description");
            Set<UUID> participants = Set.of();

            assertThrows(MeetingWithoutParticipantsException.class,
                    () -> new MeetingDetails(title, description, participants));
        }

        @Test
        @DisplayName("Should create with single participant")
        void shouldCreateWithSingleParticipant() {
            MeetingTitle title = new MeetingTitle("One on One");
            MeetingDescription description = new MeetingDescription("Performance Review");
            Set<UUID> participants = Set.of(UUID.randomUUID());

            MeetingDetails details = new MeetingDetails(title, description, participants);

            assertEquals(1, details.participants().size());
        }

        @Test
        @DisplayName("Should create with multiple participants")
        void shouldCreateWithMultipleParticipants() {
            MeetingTitle title = new MeetingTitle("All Hands");
            MeetingDescription description = new MeetingDescription("Company Update");
            Set<UUID> participants = Set.of(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID()
            );

            MeetingDetails details = new MeetingDetails(title, description, participants);

            assertEquals(3, details.participants().size());
        }
    }

    @Nested
    @DisplayName("MeetingDetails Accessors")
    class AccessorTests {

        @Test
        @DisplayName("Should return correct title")
        void shouldReturnCorrectTitle() {
            MeetingTitle title = new MeetingTitle("Meeting Title");
            MeetingDescription description = new MeetingDescription("Description");
            Set<UUID> participants = Set.of(UUID.randomUUID());

            MeetingDetails details = new MeetingDetails(title, description, participants);

            assertEquals(title, details.meetingTitle());
        }

        @Test
        @DisplayName("Should return correct description")
        void shouldReturnCorrectDescription() {
            MeetingTitle title = new MeetingTitle("Title");
            MeetingDescription description = new MeetingDescription("Meeting Description");
            Set<UUID> participants = Set.of(UUID.randomUUID());

            MeetingDetails details = new MeetingDetails(title, description, participants);

            assertEquals(description, details.meetingDescription());
        }

        @Test
        @DisplayName("Should return defensive copy of participants")
        void shouldReturnDefensiveCopyOfParticipants() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Description"),
                    Set.of(UUID.randomUUID())
            );
            assertThrows(UnsupportedOperationException.class,
                    () -> details.participants().add(UUID.randomUUID()));

            assertEquals(1, details.participants().size());
        }
    }

    @Nested
    @DisplayName("MeetingDetails Immutability")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should have value-based equality")
        void shouldHaveValueBasedEquality() {
            UUID p1 = UUID.randomUUID();
            UUID p2 = UUID.randomUUID();

            MeetingDetails details1 = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(p1, p2)
            );

            MeetingDetails details2 = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(p1, p2)
            );

            assertEquals(details1, details2);
        }

        @Test
        @DisplayName("Should have consistent hash code for equal values")
        void shouldHaveConsistentHashCodeForEqualValues() {
            UUID participant = UUID.randomUUID();

            MeetingDetails details1 = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(participant)
            );

            MeetingDetails details2 = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(participant)
            );

            assertEquals(details1.hashCode(), details2.hashCode());
        }
    }
}
