package com.doodle.scheduler.application.config.usecase.deletetimeslot;

import com.doodle.scheduler.application.config.usecase.BaseUseCaseConfigTest;
import com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators.LoggedDeleteTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.config.usecase.deletetimeslot.decorators.TransactionalDeleteTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.domain.calendar.port.in.deletetimeslot.DeleteTimeSlotUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DeleteTimeSlotUseCaseConfig - Decorator Wiring Test")
class DeleteTimeSlotUseCaseConfigTest extends BaseUseCaseConfigTest {

    @Autowired
    private DeleteTimeSlotUseCase deleteTimeSlotUseCase;

    @Test
    @DisplayName("Should wire decorators in correct order: Logged -> Transactional -> Core")
    void shouldWireDecoratorsInCorrectOrder() {
        // Given & When
        final var logged = deleteTimeSlotUseCase;

        // Then - verify the chain
        assertThat(logged)
                .as("Primary bean should be LoggedDeleteTimeSlotUseCaseDecorator")
                .isInstanceOf(LoggedDeleteTimeSlotUseCaseDecorator.class);

        final Object transactional = ReflectionTestUtils.getField(logged, "delegate");
        assertThat(transactional)
                .as("Second layer should be TransactionalDeleteTimeSlotUseCaseDecorator")
                .isNotNull()
                .isInstanceOf(TransactionalDeleteTimeSlotUseCaseDecorator.class);

        final Object core = ReflectionTestUtils.getField(transactional, "delegate");
        assertThat(core)
                .as("Third layer should be core DeleteTimeSlotUseCase implementation")
                .isNotNull()
                .isInstanceOf(DeleteTimeSlotUseCase.class);
    }
}
