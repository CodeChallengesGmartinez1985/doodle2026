package com.doodle.scheduler.application.e2e;

import com.doodle.scheduler.application.adapter.in.rest.common.dto.PaginationDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchFiltersTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class SearchTimeSlotsIT extends BaseE2E {

    private static final String SEARCH_ENDPOINT = "/api/v1/timeslots/search";

    @Test
    @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testSearchAllTimeSlotsWithPagination() {
        // GIVEN
        SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(
                new SearchFiltersTimeSlotRequestDto(),
                new PaginationDto(0, 10)
        );

        // WHEN
        ResponseEntity<SearchTimeSlotsResponseDto> response = whenPostSearch(requestDto);

        // THEN
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isNotNull();

        SearchTimeSlotsResponseDto responseBody = response.getBody();
        then(responseBody.getTotalElements()).isEqualTo(12);
        then(responseBody.getTotalPages()).isEqualTo(2);
        then(responseBody.getCurrentPage()).isEqualTo(0);
        then(responseBody.getPageSize()).isEqualTo(10);
        then(responseBody.getTimeSlots()).hasSize(10);
    }

    @Test
    @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testSearchWithFilters() {
        // GIVEN - AVAILABLE slots in Feb 8-9, 2026
        Instant startTime = Instant.parse("2026-02-08T00:00:00Z");
        Instant endTime = Instant.parse("2026-02-09T23:59:59Z");
        SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto(
                "AVAILABLE",
                startTime,
                endTime
        );
        SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(
                filters,
                new PaginationDto(0, 10)
        );

        // WHEN
        ResponseEntity<SearchTimeSlotsResponseDto> response = whenPostSearch(requestDto);

        // THEN
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isNotNull();

        SearchTimeSlotsResponseDto responseBody = response.getBody();
        then(responseBody.getTotalElements()).isEqualTo(5);
        then(responseBody.getTimeSlots()).hasSize(5);

        // Verify all constraints
        responseBody.getTimeSlots().forEach(slot -> {
            then(slot.getState()).isEqualTo("AVAILABLE");
            then(slot.getStart()).isAfterOrEqualTo(startTime);
            then(slot.getEnd()).isBeforeOrEqualTo(endTime);
        });
    }

    @Test
    @Sql(scripts = "/sql/timeslot/seed-user-with-search-timeslots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/timeslot/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void testSearchReturnsEmptyWhenNoMatch() {
        // GIVEN - search for slots in future date with no data
        Instant startTime = Instant.parse("2026-03-01T00:00:00Z");
        Instant endTime = Instant.parse("2026-03-31T23:59:59Z");
        SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto(
                null,
                startTime,
                endTime
        );
        SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(
                filters,
                new PaginationDto(0, 10)
        );

        // WHEN
        ResponseEntity<SearchTimeSlotsResponseDto> response = whenPostSearch(requestDto);

        // THEN
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isNotNull();

        SearchTimeSlotsResponseDto responseBody = response.getBody();
        then(responseBody.getTotalElements()).isEqualTo(0);
        then(responseBody.getTimeSlots()).isEmpty();
    }

    private ResponseEntity<SearchTimeSlotsResponseDto> whenPostSearch(SearchTimeSlotsRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<SearchTimeSlotsRequestDto> request = new HttpEntity<>(requestDto, headers);

        return restTemplate.exchange(
                SEARCH_ENDPOINT,
                HttpMethod.POST,
                request,
                SearchTimeSlotsResponseDto.class
        );
    }
}
