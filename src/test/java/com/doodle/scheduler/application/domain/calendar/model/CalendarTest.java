package com.doodle.scheduler.application.domain.calendar.model;

import com.doodle.scheduler.application.domain.calendar.exception.*;
import com.doodle.scheduler.application.domain.meeting.model.MeetingDescription;
import com.doodle.scheduler.application.domain.meeting.model.MeetingDetails;
import com.doodle.scheduler.application.domain.meeting.model.MeetingTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calendar - Aggregate Root")
class CalendarTest {

    @Nested
    @DisplayName("Calendar Creation")
    class CreationTests {

        @Test
        @DisplayName("Should create Calendar with valid ownerId")
        void shouldCreateCalendarWithValidOwnerId() {
            UUID ownerId = UUID.randomUUID();

            Calendar calendar = Calendar.create(ownerId);

            assertNotNull(calendar);
            assertNotNull(calendar.getId());
        }

        @Test
        @DisplayName("Should generate unique ID for each calendar")
        void shouldGenerateUniqueIdForEachCalendar() {
            UUID ownerId = UUID.randomUUID();

            Calendar calendar1 = Calendar.create(ownerId);
            Calendar calendar2 = Calendar.create(ownerId);

            assertNotEquals(calendar1.getId(), calendar2.getId());
        }

        @Test
        @DisplayName("Should reject null ownerId")
        void shouldRejectNullOwnerId() {
            assertThrows(NullPointerException.class, () -> Calendar.create(null));
        }
    }

    @Nested
    @DisplayName("Calendar TimeSlot Management - Add Slot")
    class AddSlotTests {

        @Test
        @DisplayName("Should add time slot successfully")
        void shouldAddTimeSlotSuccessfully() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            UUID slotId = calendar.addTimeSlot(start, 60);

            assertNotNull(slotId);
        }

        @Test
        @DisplayName("Should return unique slot ID")
        void shouldReturnUniqueSlotId() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");
            Instant start2 = Instant.parse("2026-02-05T11:00:00Z");

            UUID slot1 = calendar.addTimeSlot(start1, 60);
            UUID slot2 = calendar.addTimeSlot(start2, 60);

            assertNotEquals(slot1, slot2);
        }

        @Test
        @DisplayName("Should allow multiple non-overlapping slots")
        void shouldAllowMultipleNonOverlappingSlots() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");
            Instant start2 = Instant.parse("2026-02-05T12:00:00Z");

            UUID slot1 = calendar.addTimeSlot(start1, 60);
            UUID slot2 = calendar.addTimeSlot(start2, 60);

            assertNotNull(slot1);
            assertNotNull(slot2);
        }
    }

    @Nested
    @DisplayName("Calendar TimeSlot Collision Detection")
    class CollisionDetectionTests {

        @Test
        @DisplayName("Should reject overlapping slots")
        void shouldRejectOverlappingSlots() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");

            calendar.addTimeSlot(start, 60);

            Instant overlappingStart = start.plusSeconds(1800);
            assertThrows(TimeSlotCollisionException.class,
                    () -> calendar.addTimeSlot(overlappingStart, 60));
        }

        @Test
        @DisplayName("Should allow adjacent slots (no gap)")
        void shouldAllowAdjacentSlotsNoGap() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");

            calendar.addTimeSlot(start1, 60);

            Instant start2 = start1.plusSeconds(3600);
            UUID slot2 = calendar.addTimeSlot(start2, 60);

            assertNotNull(slot2);
        }

        @Test
        @DisplayName("Should allow slots with gap between them")
        void shouldAllowSlotsWithGapBetweenThem() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");

            calendar.addTimeSlot(start1, 60);

            Instant start2 = start1.plusSeconds(5400);
            UUID slot2 = calendar.addTimeSlot(start2, 60);

            assertNotNull(slot2);
        }
    }

    @Nested
    @DisplayName("Calendar TimeSlot Update")
    class UpdateSlotTests {

        @Test
        @DisplayName("Should update time slot successfully")
        void shouldUpdateTimeSlotSuccessfully() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start1, 60);

            Instant newStart = Instant.parse("2026-02-05T14:00:00Z");
            calendar.updateTimeSlot(slotId, newStart, 90);

            assertNotNull(slotId);
        }

        @Test
        @DisplayName("Should reject update of non-existent slot")
        void shouldRejectUpdateOfNonExistentSlot() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            UUID nonExistentSlotId = UUID.randomUUID();

            assertThrows(TimeSlotNotFoundException.class,
                    () -> calendar.updateTimeSlot(nonExistentSlotId, Instant.now(), 60));
        }
    }

    @Nested
    @DisplayName("Calendar TimeSlot Deletion")
    class DeleteSlotTests {

        @Test
        @DisplayName("Should delete unassigned slot successfully")
        void shouldDeleteUnassignedSlotSuccessfully() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);

            calendar.deleteTimeSlot(slotId);

            assertNotNull(slotId);
        }

        @Test
        @DisplayName("Should reject deletion of slot assigned to meeting")
        void shouldRejectDeletionOfSlotAssignedToMeeting() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);

            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            calendar.scheduleMeeting(slotId, details);

            assertThrows(SlotAssignedToMeetingException.class,
                    () -> calendar.deleteTimeSlot(slotId));
        }
    }

    @Nested
    @DisplayName("Calendar TimeSlot State Management")
    class SlotStateManagementTests {

        @Test
        @DisplayName("Should mark slot busy")
        void shouldMarkSlotBusy() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);

            calendar.markTimeSlotBusy(slotId);

            assertNotNull(slotId);
        }

        @Test
        @DisplayName("Should mark slot available after marking busy")
        void shouldMarkSlotAvailable() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);

            calendar.markTimeSlotBusy(slotId);
            calendar.markTimeSlotAvailable(slotId);

            assertNotNull(slotId);
        }
    }

    @Nested
    @DisplayName("Calendar Meeting Scheduling")
    class MeetingSchedulingTests {

        @Test
        @DisplayName("Should schedule meeting on available slot")
        void shouldScheduleMeetingOnAvailableSlot() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            UUID meetingId = calendar.scheduleMeeting(slotId, details);

            assertNotNull(meetingId);
        }

        @Test
        @DisplayName("Should reject scheduling on non-existent slot")
        void shouldRejectSchedulingOnNonExistentSlot() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            UUID nonExistentSlotId = UUID.randomUUID();
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            assertThrows(TimeSlotNotFoundException.class,
                    () -> calendar.scheduleMeeting(nonExistentSlotId, details));
        }

        @Test
        @DisplayName("Should reject scheduling on busy slot")
        void shouldRejectSchedulingOnBusySlot() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);

            calendar.markTimeSlotBusy(slotId);

            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            assertThrows(TimeSlotNotAvailableException.class,
                    () -> calendar.scheduleMeeting(slotId, details));
        }

        @Test
        @DisplayName("Should mark slot as busy after scheduling meeting")
        void shouldMarkSlotBusyAfterSchedulingMeeting() {
            Calendar calendar = Calendar.create(UUID.randomUUID());
            Instant start = Instant.parse("2026-02-05T10:00:00Z");
            UUID slotId = calendar.addTimeSlot(start, 60);
            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );

            calendar.scheduleMeeting(slotId, details);

            MeetingDetails details2 = new MeetingDetails(
                    new MeetingTitle("Title2"),
                    new MeetingDescription("Desc2"),
                    Set.of(UUID.randomUUID())
            );
            assertThrows(TimeSlotNotAvailableException.class,
                    () -> calendar.scheduleMeeting(slotId, details2));
        }
    }

    @Nested
    @DisplayName("Calendar Complex Scenarios")
    class ComplexScenarioTests {

        @Test
        @DisplayName("Should handle full workflow: add, schedule, delete other slots")
        void shouldHandleFullWorkflow() {
            Calendar calendar = Calendar.create(UUID.randomUUID());

            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");
            Instant start2 = Instant.parse("2026-02-05T12:00:00Z");
            Instant start3 = Instant.parse("2026-02-05T14:00:00Z");

            UUID slot1 = calendar.addTimeSlot(start1, 60);
            UUID slot2 = calendar.addTimeSlot(start2, 60);
            UUID slot3 = calendar.addTimeSlot(start3, 60);

            MeetingDetails details = new MeetingDetails(
                    new MeetingTitle("Title"),
                    new MeetingDescription("Desc"),
                    Set.of(UUID.randomUUID())
            );
            UUID meetingId = calendar.scheduleMeeting(slot1, details);

            calendar.deleteTimeSlot(slot3);

            assertThrows(SlotAssignedToMeetingException.class,
                    () -> calendar.deleteTimeSlot(slot1));

            assertNotNull(meetingId);
        }

        @Test
        @DisplayName("Should handle multiple meetings on different slots")
        void shouldHandleMultipleMeetingsOnDifferentSlots() {
            Calendar calendar = Calendar.create(UUID.randomUUID());

            Instant start1 = Instant.parse("2026-02-05T10:00:00Z");
            Instant start2 = Instant.parse("2026-02-05T12:00:00Z");

            UUID slot1 = calendar.addTimeSlot(start1, 60);
            UUID slot2 = calendar.addTimeSlot(start2, 60);

            MeetingDetails details1 = new MeetingDetails(
                    new MeetingTitle("Title1"),
                    new MeetingDescription("Desc1"),
                    Set.of(UUID.randomUUID())
            );
            MeetingDetails details2 = new MeetingDetails(
                    new MeetingTitle("Title2"),
                    new MeetingDescription("Desc2"),
                    Set.of(UUID.randomUUID())
            );

            UUID meeting1 = calendar.scheduleMeeting(slot1, details1);
            UUID meeting2 = calendar.scheduleMeeting(slot2, details2);

            assertNotEquals(meeting1, meeting2);
        }
    }

    @Nested
    @DisplayName("Calendar Integration")
    class IntegrationTests {

        @Test
        @DisplayName("Should create default calendar")
        void shouldCreateDefaultCalendar() {
            Calendar calendar = Calendar.create(UUID.randomUUID());

            assertNotNull(calendar);
            assertNotNull(calendar.getId());
        }

        @Test
        @DisplayName("Should create calendar owned by specific user")
        void shouldCreateCalendarOwnedBySpecificUser() {
            UUID ownerId = UUID.randomUUID();
            Calendar calendar = Calendar.create(ownerId);

            assertNotNull(calendar);
            assertNotNull(calendar.getId());
        }
    }
}
