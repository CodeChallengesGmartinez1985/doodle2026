package com.doodle.scheduler.application.adapter.out.persistence.timeslot;

import com.doodle.scheduler.application.adapter.out.persistence.BaseJpaSliceTest;
import com.doodle.scheduler.application.adapter.out.persistence.timeslot.common.TimeSlotJpaMapperImpl;
import com.doodle.scheduler.application.domain.calendar.model.Calendar;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import({SaveTimeSlotRepositoryAdapter.class, TimeSlotJpaMapperImpl.class})
@DisplayName("SaveTimeSlotRepositoryAdapter - Slice Test")
class SaveTimeSlotRepositoryAdapterSliceTest extends BaseJpaSliceTest {

    @Autowired
    private SaveTimeSlotRepositoryAdapter saveAdapter;

    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @Test
    @DisplayName("GIVEN new TimeSlot WHEN saveTimeSlot THEN returns persisted TimeSlot with all fields correctly mapped")
    @Sql(scripts = "/sql/timeslot/seed-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldSaveNewTimeSlotAndReturnCompleteObject() {
        // GIVEN
        Instant start = Instant.parse("2026-02-07T10:00:00Z");
        int duration = 60;
        Calendar calendar = Calendar.create(TEST_USER_ID);
        TimeSlot timeSlot = calendar.addTimeSlot(start, duration);
        UUID originalId = timeSlot.getId();
        // WHEN
        TimeSlot saved = saveAdapter.saveTimeSlot(timeSlot);
        // THEN
        assertNotNull(saved, "Saved TimeSlot should not be null");
        assertEquals(originalId, saved.getId(), "TimeSlot ID should be preserved");
        assertEquals(start, saved.getRange().start(), "Start time should be preserved");
        assertEquals(duration, saved.getDurationMinutes(), "Duration should be preserved");
        assertEquals(TEST_USER_ID, saved.getOwnerId(), "Owner ID should be preserved");
        assertEquals("AVAILABLE", saved.getStateString(), "Default state should be AVAILABLE");
    }
}
