package com.doodle.scheduler.application.domain.calendar.model;

import com.doodle.scheduler.application.domain.common.model.AggregateRoot;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotInvalidIdException;
import com.doodle.scheduler.application.domain.calendar.exception.SlotAssignedToMeetingException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotAvailableException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotCollisionException;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotFoundException;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeRange;
import com.doodle.scheduler.application.domain.meeting.model.Meeting;
import com.doodle.scheduler.application.domain.meeting.model.MeetingDetails;

import java.time.Instant;
import java.util.*;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Calendar extends AggregateRoot {
    /**
     * Attributes
     */
    private final UUID ownerId;

    private final Map<UUID, TimeSlot> slots = new HashMap<>();

    private final NavigableSet<TimeSlot> slotsByStart = new TreeSet<>(
            Comparator
                    .comparing((TimeSlot s) -> s.getRange().start())
                    .thenComparing(TimeSlot::getId)
    );

    private final List<Meeting> meetings = new ArrayList<>();

    /**
     * Public API
     */
    public static Calendar create(UUID ownerId) {
        return new Calendar(UUID.randomUUID(), ownerId);
    }

    public UUID addTimeSlot(Instant start, int durationMinutes) {
        UUID id = UUID.randomUUID();
        TimeSlot candidate = TimeSlot.create(id, start, durationMinutes);
        validateNoOverlap(candidate, null);
        if (slots.containsKey(id)) {
            throw new TimeSlotInvalidIdException("time slot id collision: " + id);
        }
        slots.put(id, candidate);
        slotsByStart.add(candidate);
        return id;
    }

    public void updateTimeSlot(UUID slotId, Instant start, int durationMinutes) {
        TimeSlot slot = findSlotOrThrow(slotId);
        slotsByStart.remove(slot);
        try {
            TimeSlot candidate = TimeSlot.create(slotId, start, durationMinutes);
            validateNoOverlap(candidate, slotId);
            slot.changeTimeRange(start, durationMinutes);
        } finally {
            slotsByStart.add(slot);
        }
    }

    public void deleteTimeSlot(UUID slotId) {
        Objects.requireNonNull(slotId, "slotId must not be null");
        boolean isSlotAssignedToMeeting = meetings.stream().anyMatch(m -> m.getSlotIds().contains(slotId));
        if (isSlotAssignedToMeeting) {
            throw new SlotAssignedToMeetingException("time slot is used by a meeting and cannot be deleted");
        }
        TimeSlot removed = slots.remove(slotId);
        if (removed != null) {
            slotsByStart.remove(removed);
        }
    }

    public void markTimeSlotBusy(UUID slotId) {
        TimeSlot slot = findSlotOrThrow(slotId);
        slot.markBusy();
    }

    public void markTimeSlotAvailable(UUID slotId) {
        TimeSlot slot = findSlotOrThrow(slotId);
        slot.markAvailable();
    }

    public UUID scheduleMeeting(UUID slotId, MeetingDetails details) {
        TimeSlot slot = findSlotOrThrow(slotId);
        if (!slot.getState().isAvailable()) {
            throw new TimeSlotNotAvailableException("time slot is not available");
        }
        slot.markBusy();
        Meeting meeting = Meeting.create(details, List.of(slotId));
        meetings.add(meeting);
        return meeting.getId();
    }

    /**
     * Private methods / constructors
     */
    private Calendar(UUID id, UUID ownerId) {
        super(id);
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId must not be null");
    }

    private void validateNoOverlap(TimeSlot candidate, UUID ignoreSlotId) {
        checkNeighbor(candidate, ignoreSlotId, slotsByStart.floor(candidate));
        checkNeighbor(candidate, ignoreSlotId, slotsByStart.ceiling(candidate));
    }

    private void checkNeighbor(TimeSlot candidate, UUID ignoreSlotId, TimeSlot neighbor) {
        if (neighbor == null) return;
        if (Objects.equals(neighbor.getId(), ignoreSlotId)) return;
        if (!overlaps(neighbor.getRange(), candidate.getRange())) return;

        throw new TimeSlotCollisionException(
                "time slot overlaps an existing slot: " + neighbor.getId()
        );
    }

    private boolean overlaps(TimeRange a, TimeRange b) {
        return a.start().isBefore(b.end()) && a.end().isAfter(b.start());
    }

    private TimeSlot findSlotOrThrow(UUID slotId) {
        TimeSlot slot = slots.get(slotId);
        if (slot == null) throw new TimeSlotNotFoundException("time slot not found: " + slotId);
        return slot;
    }
}
