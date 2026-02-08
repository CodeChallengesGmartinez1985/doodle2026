package com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots;

import com.doodle.scheduler.application.adapter.in.rest.BaseRestTest;
import com.doodle.scheduler.application.adapter.in.rest.common.dto.PaginationDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchTimeSlotsRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.dto.SearchFiltersTimeSlotRequestDto;
import com.doodle.scheduler.application.adapter.in.rest.timeslot.searchtimeslots.mapper.SearchTimeSlotsDtoMapperImpl;
import com.doodle.scheduler.application.domain.calendar.port.in.SearchTimeSlotsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {SearchTimeSlotsController.class})
@Import(SearchTimeSlotsDtoMapperImpl.class)
@DisplayName("SearchTimeSlotsController")
class SearchTimeSlotsControllerSliceTest extends BaseRestTest {

    @MockitoBean
    private SearchTimeSlotsUseCase searchTimeSlotsUseCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("should return 200 with empty list when no time slots match")
        void shouldReturn200WithEmptyList() throws Exception {
            // Given
            SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto();

            SearchTimeSlotsUseCase.SearchTimeSlotsResult result =
                new SearchTimeSlotsUseCase.SearchTimeSlotsResult(
                    Collections.emptyList(),
                    0L,
                    0,
                    0,
                    10
                );

            when(searchTimeSlotsUseCase.execute(any())).thenReturn(result);

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.time_slots").isArray())
                    .andExpect(jsonPath("$.time_slots").isEmpty())
                    .andExpect(jsonPath("$.total_elements").value(0))
                    .andExpect(jsonPath("$.total_pages").value(0))
                    .andExpect(jsonPath("$.current_page").value(0))
                    .andExpect(jsonPath("$.page_size").value(10));
        }

        @Test
        @DisplayName("should return 200 with filtered results by status")
        void shouldReturn200WithFilteredByStatus() throws Exception {
            // Given
            SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto("AVAILABLE", null, null);
            PaginationDto pagination = new PaginationDto(0, 10);
            SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(filters, pagination);

            SearchTimeSlotsUseCase.SearchTimeSlotsResult result =
                new SearchTimeSlotsUseCase.SearchTimeSlotsResult(
                    Collections.emptyList(),
                    5L,
                    1,
                    0,
                    10
                );

            when(searchTimeSlotsUseCase.execute(any())).thenReturn(result);

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total_elements").value(5));
        }

        @Test
        @DisplayName("should return 200 with time range filter")
        void shouldReturn200WithTimeRangeFilter() throws Exception {
            // Given
            Instant startTime = Instant.parse("2026-02-08T00:00:00Z");
            Instant endTime = Instant.parse("2026-02-15T23:59:59Z");

            SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto(null, startTime, endTime);
            PaginationDto pagination = new PaginationDto(0, 10);
            SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(filters, pagination);

            SearchTimeSlotsUseCase.SearchTimeSlotsResult result =
                new SearchTimeSlotsUseCase.SearchTimeSlotsResult(
                    Collections.emptyList(),
                    3L,
                    1,
                    0,
                    10
                );

            when(searchTimeSlotsUseCase.execute(any())).thenReturn(result);

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total_elements").value(3));
        }
    }

    @Nested
    @DisplayName("Validation Error Scenarios")
    class ValidationErrorScenarios {

        @Test
        @DisplayName("should return 400 when status is invalid")
        void shouldReturn400WhenStatusIsInvalid() throws Exception {
            // Given
            String invalidRequest = """
                {
                    "filters": {
                        "status": "INVALID_STATUS"
                    },
                    "pagination": {
                        "page": 0,
                        "size": 10
                    }
                }
                """;

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when page is negative")
        void shouldReturn400WhenPageIsNegative() throws Exception {
            // Given
            SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto(null, null, null);
            PaginationDto pagination = new PaginationDto(-1, 10);
            SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(filters, pagination);

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when size is zero or negative")
        void shouldReturn400WhenSizeIsZeroOrNegative() throws Exception {
            // Given
            SearchFiltersTimeSlotRequestDto filters = new SearchFiltersTimeSlotRequestDto(null, null, null);
            PaginationDto pagination = new PaginationDto(0, 0);
            SearchTimeSlotsRequestDto requestDto = new SearchTimeSlotsRequestDto(filters, pagination);

            // When & Then
            mockMvc.perform(post("/api/v1/timeslots/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
