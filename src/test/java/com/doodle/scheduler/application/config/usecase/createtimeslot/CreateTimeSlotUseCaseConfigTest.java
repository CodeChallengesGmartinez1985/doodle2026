package com.doodle.scheduler.application.config.usecase.createtimeslot;

import com.doodle.scheduler.application.config.usecase.BaseUseCaseConfigTest;
import com.doodle.scheduler.application.config.usecase.createtimeslot.decorators.LoggedCreateTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.config.usecase.createtimeslot.decorators.TransactionalCreateTimeSlotUseCaseDecorator;
import com.doodle.scheduler.application.domain.calendar.port.in.CreateTimeSlotUseCase;
import com.doodle.scheduler.application.domain.calendar.service.CreateTimeSlotServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CreateTimeSlotUseCaseConfig - Decorator Wiring Test")
class CreateTimeSlotUseCaseConfigTest extends BaseUseCaseConfigTest {

    @Autowired
    private CreateTimeSlotUseCase createTimeSlotUseCase;

    @Test
    @DisplayName("GIVEN configured use case bean WHEN inspecting decorator chain THEN should have correct wiring order")
    void shouldHaveCorrectDecoratorWiringOrder() throws Exception {
        final var logged = createTimeSlotUseCase;
        assertThat(logged)
                .as("Primary bean should be LoggedCreateTimeSlotUseCaseDecorator")
                .isInstanceOf(LoggedCreateTimeSlotUseCaseDecorator.class);

        final var transactional = getDelegate(logged);
        assertThat(transactional)
                .as("Second layer should be TransactionalCreateTimeSlotUseCaseDecorator")
                .isInstanceOf(TransactionalCreateTimeSlotUseCaseDecorator.class);

        final var service = getDelegate(transactional);
        assertThat(service)
                .as("Core layer should be CreateTimeSlotServiceImpl")
                .isInstanceOf(CreateTimeSlotServiceImpl.class);

        assertThrows(NoSuchFieldException.class,
                () -> getDelegate(service),
                "Core service should not have a delegate field");
    }

    /**
     * Extracts the delegate field from a decorator, unwrapping any proxies.
     *
     * @param target the decorator instance
     * @return the unwrapped delegate
     * @throws Exception if delegate field is not found or cannot be accessed
     */
    private Object getDelegate(Object target) throws Exception {
        final Object current = unwrapProxy(target);

        Class<?> clazz = current.getClass();
        while (clazz != null) {
            try {
                final Field field = clazz.getDeclaredField("delegate");
                field.setAccessible(true);
                final Object value = field.get(current);
                return value == null ? null : unwrapProxy(value);
            } catch (final NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("No delegate field found in " + current.getClass());
    }

    /**
     * Unwraps Spring AOP proxies to get the actual target object.
     *
     * @param candidate the potentially proxied object
     * @return the unwrapped target object
     */
    private Object unwrapProxy(Object candidate) {
        return AopTestUtils.getTargetObject(candidate);
    }
}
