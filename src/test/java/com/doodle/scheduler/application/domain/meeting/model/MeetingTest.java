package com.doodle.scheduler.application.domain.meeting.model;

import com.doodle.scheduler.application.domain.meeting.exception.MeetingCreationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Meeting - Entity")
class MeetingTest {

    @Nested
    @DisplayName("Meeting Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create Meeting with valid details and single slot")
        void shouldCreateMeetingWithValidDetailsAndSlot() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID slotId = UUID.randomUUID();

            Meeting meeting = Meeting.create(details, List.of(slotId));

            assertNotNull(meeting);
            assertNotNull(meeting.getId());
            assertEquals(1, meeting.getSlotIds().size());
            assertTrue(meeting.getSlotIds().contains(slotId));
        }

        @Test
        @DisplayName("Should reject Meeting with multiple slots")
        void shouldRejectMeetingWithMultipleSlots() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID slot1 = UUID.randomUUID();
            UUID slot2 = UUID.randomUUID();
            UUID slot3 = UUID.randomUUID();

            assertThrows(MeetingCreationException.class,
                    () -> Meeting.create(details, List.of(slot1, slot2, slot3)));
        }

        @Test
        @DisplayName("Should reject null details")
        void shouldRejectNullDetails() {
            assertThrows(NullPointerException.class,
                    () -> Meeting.create(null, List.of(UUID.randomUUID())));
        }

        @Test
        @DisplayName("Should reject null slot list")
        void shouldRejectNullSlotList() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            assertThrows(NullPointerException.class, () -> Meeting.create(details, null));
        }

        @Test
        @DisplayName("Should reject empty slot list")
        void shouldRejectEmptySlotList() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            assertThrows(MeetingCreationException.class, () -> Meeting.create(details, List.of()));
        }

        @Test
        @DisplayName("Should generate unique UUID for each meeting")
        void shouldGenerateUniqueUuidForEachMeeting() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID slotId = UUID.randomUUID();

            Meeting meeting1 = Meeting.create(details, List.of(slotId));
            Meeting meeting2 = Meeting.create(details, List.of(slotId));

            assertNotEquals(meeting1.getId(), meeting2.getId());
        }
    }

    @Nested
    @DisplayName("Meeting Accessors")
    class AccessorTests {

        @Test
        @DisplayName("Should return defensive copy of slot IDs")
        void shouldReturnDefensiveCopyOfSlotIds() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID slot1 = UUID.randomUUID();

            Meeting meeting = Meeting.create(details, List.of(slot1));

            List<UUID> slotIds = meeting.getSlotIds();

            assertThrows(UnsupportedOperationException.class,
                    () -> slotIds.add(UUID.randomUUID()));

            assertEquals(1, meeting.getSlotIds().size());
        }

        @Test
        @DisplayName("Should return correct single slot ID")
        void shouldReturnCorrectSlotId() {
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID slot1 = UUID.randomUUID();

            Meeting meeting = Meeting.create(details, List.of(slot1));

            List<UUID> slotIds = meeting.getSlotIds();
            assertEquals(1, slotIds.size());
            assertTrue(slotIds.contains(slot1));
        }
    }
}
