package com.doodle.scheduler.e2e;

import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.CreateTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.createtimeslot.dto.TimeSlotResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class CreateTimeSlotIT extends BaseE2E {

    private static final String TIME_SLOTS_ENDPOINT = "/api/v1/timeslots";

    @Test
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testCreateTimeSlot() {
        // GIVEN
        Instant start = Instant.parse("2026-02-07T10:00:00Z");
        Integer durationMinutes = 60;
        CreateTimeSlotRequestDto requestDto = new CreateTimeSlotRequestDto(start, durationMinutes);

        // WHEN
        ResponseEntity<TimeSlotResponseDto> response = whenPostTimeSlot(requestDto);

        // THEN
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNotNull();

        TimeSlotResponseDto responseBody = response.getBody();
        then(responseBody.getId()).isNotNull();
        then(responseBody.getStart()).isEqualTo(start);
        then(responseBody.getEnd()).isEqualTo(start.plusSeconds(durationMinutes * 60L));
        then(responseBody.getDurationMinutes()).isEqualTo(durationMinutes);
        then(responseBody.getState()).isEqualTo("AVAILABLE");
    }

    private ResponseEntity<TimeSlotResponseDto> whenPostTimeSlot(CreateTimeSlotRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<CreateTimeSlotRequestDto> request = new HttpEntity<>(requestDto, headers);

        return restTemplate.exchange(
                TIME_SLOTS_ENDPOINT,
                HttpMethod.POST,
                request,
                TimeSlotResponseDto.class
        );
    }
}
