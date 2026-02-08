package com.doodle.scheduler.application.domain.calendar.service;

import com.doodle.scheduler.application.domain.calendar.port.in.createtimeslot.CreateTimeSlotCommand;
import com.doodle.scheduler.application.domain.calendar.model.timeslot.TimeSlot;
import com.doodle.scheduler.application.domain.calendar.port.out.searchtimeslots.LoadTimeSlotsByUserPort;
import com.doodle.scheduler.application.domain.calendar.port.out.createtimeslot.SaveTimeSlotPort;
import com.doodle.scheduler.application.domain.common.events.Publisher;
import com.doodle.scheduler.application.domain.common.events.Subscriber;
import com.doodle.scheduler.application.domain.common.events.TimeSlotCreatedEvent;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTimeSlotServiceImpl Unit Tests")
class CreateTimeSlotServiceImplTest {

    @Mock
    private LoadUserByUsernamePort loadUserByUsernamePort;

    @Mock
    private LoadTimeSlotsByUserPort loadTimeSlotsByUserPort;

    @Mock
    private SaveTimeSlotPort saveTimeSlotPort;

    @Mock
    private Subscriber<TimeSlotCreatedEvent> eventSubscriber;

    private CreateTimeSlotServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CreateTimeSlotServiceImpl(
                loadUserByUsernamePort,
                loadTimeSlotsByUserPort,
                saveTimeSlotPort
        );

        Publisher.INSTANCE.attach(TimeSlotCreatedEvent.class, eventSubscriber);
    }

    @Test
    @DisplayName("Should publish TimeSlotCreatedEvent after successfully creating and saving time slot")
    void shouldPublishEventAfterCreatingTimeSlot() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        CreateTimeSlotCommand command = new CreateTimeSlotCommand(username, start, durationMinutes);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotsByUserPort.loadTimeSlotsByUserId(userId)).thenReturn(List.of());
        when(saveTimeSlotPort.saveTimeSlot(any(TimeSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TimeSlot result = service.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOwnerId()).isEqualTo(userId);
        assertThat(result.getRange().start()).isEqualTo(start);

        // Verify event was published
        ArgumentCaptor<TimeSlotCreatedEvent> eventCaptor = ArgumentCaptor.forClass(TimeSlotCreatedEvent.class);
        verify(eventSubscriber, times(1)).update(eventCaptor.capture());

        TimeSlotCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent).isNotNull();
        assertThat(publishedEvent.timeSlot()).isEqualTo(result);
        assertThat(publishedEvent.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should save time slot before publishing event")
    void shouldSaveBeforePublishingEvent() {
        // Given
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        Instant start = Instant.parse("2026-02-10T10:00:00Z");
        int durationMinutes = 30;

        User user = User.reconstitute(userId, username);
        CreateTimeSlotCommand command = new CreateTimeSlotCommand(username, start, durationMinutes);

        when(loadUserByUsernamePort.loadUserByUsername(username)).thenReturn(user);
        when(loadTimeSlotsByUserPort.loadTimeSlotsByUserId(userId)).thenReturn(List.of());
        when(saveTimeSlotPort.saveTimeSlot(any(TimeSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.execute(command);

        // Then - verify order of operations
        var inOrder = inOrder(saveTimeSlotPort, eventSubscriber);
        inOrder.verify(saveTimeSlotPort).saveTimeSlot(any(TimeSlot.class));
        inOrder.verify(eventSubscriber).update(any(TimeSlotCreatedEvent.class));
    }
}
