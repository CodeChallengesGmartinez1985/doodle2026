package com.doodle.scheduler.application.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;

class DeleteTimeSlotIT extends BaseE2E {

    private static final String TIME_SLOTS_ENDPOINT = "/api/v1/timeslots";

    @Test
    @Sql(scripts = "/sql/timeslot/seed-timeslot-for-deletion.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testDeleteTimeSlot() {
        // GIVEN - A time slot exists in the database
        UUID timeSlotId = UUID.fromString("111e4567-e89b-41d4-a716-446655440001");

        // WHEN - Delete the time slot
        ResponseEntity<Void> response = whenDeleteTimeSlot(timeSlotId);

        // THEN - Should return 204 NO CONTENT
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(response.getBody()).isNull();
    }

    private ResponseEntity<Void> whenDeleteTimeSlot(UUID timeSlotId) {
        return restTemplate.exchange(
                TIME_SLOTS_ENDPOINT + "/" + timeSlotId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }
}
