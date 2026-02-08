package com.doodle.scheduler.application.domain.calendar.service;

import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.exception.TimeSlotNotFoundException;
import com.doodle.scheduler.application.domain.calendar.model.Calendar;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.deletetimeslot.DeleteTimeSlotPort;
import com.doodle.scheduler.application.domain.calendar.port.out.searchtimeslots.LoadTimeSlotByIdPort;
import com.doodle.scheduler.application.domain.common.events.Publisher;
import com.doodle.scheduler.application.domain.common.events.Subscriber;
import com.doodle.scheduler.application.domain.common.events.TimeSlotDeletedEvent;
import com.doodle.scheduler.application.domain.user.model.User;
import com.doodle.scheduler.application.domain.user.port.out.LoadUserByUsernamePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteTimeSlotServiceImpl Unit Tests")
class DeleteTimeSlotServiceImplTest {

    @Mock
    private LoadUserByUsernamePort loadUserByUsernamePort;

    @Mock
    private LoadTimeSlotByIdPort loadTimeSlotByIdPort;

    @Mock
    private DeleteTimeSlotPort deleteTimeSlotPort;

    @Mock
    private Subscriber<TimeSlotDeletedEvent> eventSubscriber;

    private DeleteTimeSlotServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeleteTimeSlotServiceImpl(
                loadUserByUsernamePort,
                loadTimeSlotByIdPort,
                deleteTimeSlotPort
        );

        Publisher.INSTANCE.attach(TimeSlotDeletedEvent.class, eventSubscriber);
    }

    @Test
    @DisplayName("Should publish TimeSlotDeletedEvent after successfully deleting time slot")
    void shouldPublishEventAfterDeletingTimeSlot() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        UUID timeSlotId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        TimeSlot timeSlot = TimeSlot.create(timeSlotId, start, durationMinutes);
        Calendar.createWithSlots(userId, List.of(timeSlot));
        DeleteTimeSlotCommand command = new DeleteTimeSlotCommand(username, timeSlotId);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotByIdPort.loadTimeSlotById(timeSlotId)).thenReturn(Optional.of(timeSlot));

        // When
        service.execute(command);

        // Then
        verify(deleteTimeSlotPort, times(1)).deleteTimeSlot(timeSlotId);

        // Verify event was published
        ArgumentCaptor<TimeSlotDeletedEvent> eventCaptor = ArgumentCaptor.forClass(TimeSlotDeletedEvent.class);
        verify(eventSubscriber, times(1)).update(eventCaptor.capture());

        TimeSlotDeletedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent).isNotNull();
        assertThat(publishedEvent.timeSlotId()).isEqualTo(timeSlotId);
        assertThat(publishedEvent.ownerId()).isEqualTo(userId);
        assertThat(publishedEvent.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should delete time slot before publishing event")
    void shouldDeleteBeforePublishingEvent() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        UUID timeSlotId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        TimeSlot timeSlot = TimeSlot.create(timeSlotId, start, durationMinutes);
        // Set calendar to establish ownership - createWithSlots modifies the timeSlot by setting its calendar
        com.doodle.scheduler.application.domain.calendar.model.Calendar.createWithSlots(userId, List.of(timeSlot));
        DeleteTimeSlotCommand command = new DeleteTimeSlotCommand(username, timeSlotId);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotByIdPort.loadTimeSlotById(timeSlotId)).thenReturn(Optional.of(timeSlot));

        // When
        service.execute(command);

        // Then - verify order of operations
        var inOrder = inOrder(deleteTimeSlotPort, eventSubscriber);
        inOrder.verify(deleteTimeSlotPort).deleteTimeSlot(timeSlotId);
        inOrder.verify(eventSubscriber).update(any(TimeSlotDeletedEvent.class));
    }

    @Test
    @DisplayName("Should throw TimeSlotNotFoundException when time slot does not exist")
    void shouldThrowExceptionWhenTimeSlotNotFound() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        UUID timeSlotId = UUID.randomUUID();

        User user = User.reconstitute(userId, username);
        DeleteTimeSlotCommand command = new DeleteTimeSlotCommand(username, timeSlotId);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotByIdPort.loadTimeSlotById(timeSlotId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(TimeSlotNotFoundException.class)
                .hasMessageContaining("Time slot not found with id: " + timeSlotId);

        verify(deleteTimeSlotPort, never()).deleteTimeSlot(any());
        verify(eventSubscriber, never()).update(any());
    }

    @Test
    @DisplayName("Should throw TimeSlotNotFoundException when time slot does not belong to user")
    void shouldThrowExceptionWhenTimeSlotDoesNotBelongToUser() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        UUID timeSlotId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        TimeSlot timeSlot = TimeSlot.create(timeSlotId, start, durationMinutes);
        // Simulate time slot belonging to another user by not setting the calendar (ownerId will be null)
        // In real scenario, the timeSlot would have a different ownerId
        DeleteTimeSlotCommand command = new DeleteTimeSlotCommand(username, timeSlotId);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotByIdPort.loadTimeSlotById(timeSlotId)).thenReturn(Optional.of(timeSlot));

        // When & Then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(TimeSlotNotFoundException.class)
                .hasMessageContaining("Time slot not found with id: " + timeSlotId);

        verify(deleteTimeSlotPort, never()).deleteTimeSlot(any());
        verify(eventSubscriber, never()).update(any());
    }

    @Test
    @DisplayName("Should not publish event when deletion fails")
    void shouldNotPublishEventWhenDeletionFails() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        UUID timeSlotId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        TimeSlot timeSlot = TimeSlot.create(timeSlotId, start, durationMinutes);
        // Set calendar to establish ownership - createWithSlots modifies the timeSlot by setting its calendar
        com.doodle.scheduler.application.domain.calendar.model.Calendar.createWithSlots(userId, List.of(timeSlot));
        DeleteTimeSlotCommand command = new DeleteTimeSlotCommand(username, timeSlotId);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotByIdPort.loadTimeSlotById(timeSlotId)).thenReturn(Optional.of(timeSlot));
        doThrow(new RuntimeException("Database error")).when(deleteTimeSlotPort).deleteTimeSlot(timeSlotId);

        // When & Then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");

        verify(eventSubscriber, never()).update(any());
    }
}
