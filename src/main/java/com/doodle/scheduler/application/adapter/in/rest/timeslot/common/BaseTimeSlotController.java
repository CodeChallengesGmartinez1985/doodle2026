package com.doodle.scheduler.application.adapter.in.rest.timeslot.common;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base controller for all time slot-related endpoints.
 * Provides common configuration for REST controllers handling time slot operations.
 *
 * <p>This abstract class centralizes:
 * <ul>
 *   <li>Base path mapping: {@code /api/v1/timeslots}</li>
 *   <li>Validation support</li>
 *   <li>Swagger documentation grouping</li>
 * </ul>
 *
 * <p>Concrete controllers should extend this class and implement specific
 * endpoints using method-level mappings (e.g., {@code @PostMapping}, {@code @GetMapping}).
 * Each controller maintains its own dependencies via constructor injection.
 */
@RequestMapping("/api/v1/timeslots")
@RequiredArgsConstructor
@Validated
@Tag(name = "Time Slots", description = "APIs for managing user time slots")
public abstract class BaseTimeSlotController {
}
