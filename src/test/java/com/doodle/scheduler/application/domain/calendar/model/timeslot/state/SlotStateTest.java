package com.doodle.scheduler.application.domain.calendar.model.timeslot.state;

import com.doodle.scheduler.application.domain.calendar.exception.InvalidSlotStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SlotState - State Pattern")
class SlotStateTest {

    @Nested
    @DisplayName("AvailableState")
    class AvailableStateTests {

        @Test
        @DisplayName("Should be singleton instance")
        void shouldBeSingletonInstance() {
            AvailableState state1 = AvailableState.INSTANCE;
            AvailableState state2 = AvailableState.INSTANCE;

            assertSame(state1, state2);
        }

        @Test
        @DisplayName("Should report isAvailable as true")
        void shouldReportIsAvailableTrue() {
            SlotState state = AvailableState.INSTANCE;

            assertTrue(state.isAvailable());
        }

        @Test
        @DisplayName("Should report isBusy as false")
        void shouldReportIsBusyFalse() {
            SlotState state = AvailableState.INSTANCE;

            assertFalse(state.isBusy());
        }

        @Test
        @DisplayName("Should transition to BusyState via markBusy")
        void shouldTransitionToBusyStateViaMarkBusy() {
            SlotState state = AvailableState.INSTANCE;

            SlotState nextState = state.markBusy();

            assertNotNull(nextState);
            assertTrue(nextState.isBusy());
            assertFalse(nextState.isAvailable());
            assertSame(BusyState.INSTANCE, nextState);
        }

        @Test
        @DisplayName("Should throw exception when calling markAvailable")
        void shouldThrowExceptionWhenCallingMarkAvailable() {
            SlotState state = AvailableState.INSTANCE;

            InvalidSlotStateTransitionException exception = assertThrows(
                    InvalidSlotStateTransitionException.class,
                    state::markAvailable
            );

            assertTrue(exception.getMessage().contains("Cannot"));
            assertTrue(exception.getMessage().contains("markAvailable"));
        }

        @Test
        @DisplayName("Should return correct state string")
        void shouldReturnCorrectStateString() {
            SlotState state = AvailableState.INSTANCE;

            assertEquals("AVAILABLE", state.getStateString());
        }

        @Test
        @DisplayName("Should return consistent state string across multiple calls")
        void shouldReturnConsistentStateString() {
            SlotState state = AvailableState.INSTANCE;

            String state1 = state.getStateString();
            String state2 = state.getStateString();
            String state3 = state.getStateString();

            assertEquals(state1, state2);
            assertEquals(state2, state3);
            assertEquals("AVAILABLE", state1);
        }
    }

    @Nested
    @DisplayName("BusyState")
    class BusyStateTests {

        @Test
        @DisplayName("Should be singleton instance")
        void shouldBeSingletonInstance() {
            BusyState state1 = BusyState.INSTANCE;
            BusyState state2 = BusyState.INSTANCE;

            assertSame(state1, state2);
        }

        @Test
        @DisplayName("Should report isAvailable as false")
        void shouldReportIsAvailableFalse() {
            SlotState state = BusyState.INSTANCE;

            assertFalse(state.isAvailable());
        }

        @Test
        @DisplayName("Should report isBusy as true")
        void shouldReportIsBusyTrue() {
            SlotState state = BusyState.INSTANCE;

            assertTrue(state.isBusy());
        }

        @Test
        @DisplayName("Should transition to AvailableState via markAvailable")
        void shouldTransitionToAvailableStateViaMarkAvailable() {
            SlotState state = BusyState.INSTANCE;

            SlotState nextState = state.markAvailable();

            assertNotNull(nextState);
            assertTrue(nextState.isAvailable());
            assertFalse(nextState.isBusy());
            assertSame(AvailableState.INSTANCE, nextState);
        }

        @Test
        @DisplayName("Should throw exception when calling markBusy")
        void shouldThrowExceptionWhenCallingMarkBusy() {
            SlotState state = BusyState.INSTANCE;

            InvalidSlotStateTransitionException exception = assertThrows(
                    InvalidSlotStateTransitionException.class,
                    state::markBusy
            );

            assertTrue(exception.getMessage().contains("Cannot"));
            assertTrue(exception.getMessage().contains("markBusy"));
        }

        @Test
        @DisplayName("Should return correct state string")
        void shouldReturnCorrectStateString() {
            SlotState state = BusyState.INSTANCE;

            assertEquals("BUSY", state.getStateString());
        }

        @Test
        @DisplayName("Should return consistent state string across multiple calls")
        void shouldReturnConsistentStateString() {
            SlotState state = BusyState.INSTANCE;

            String state1 = state.getStateString();
            String state2 = state.getStateString();
            String state3 = state.getStateString();

            assertEquals(state1, state2);
            assertEquals(state2, state3);
            assertEquals("BUSY", state1);
        }
    }

    @Nested
    @DisplayName("SlotState Transitions")
    class TransitionTests {

        @Test
        @DisplayName("Should cycle through states correctly")
        void shouldCycleThroughStatesCorrectly() {
            SlotState state = AvailableState.INSTANCE;
            assertTrue(state.isAvailable());

            state = state.markBusy();
            assertTrue(state.isBusy());

            state = state.markAvailable();
            assertTrue(state.isAvailable());

            state = state.markBusy();
            assertTrue(state.isBusy());
        }

        @Test
        @DisplayName("Should maintain correct state semantics through transitions")
        void shouldMaintainCorrectStateSemantics() {
            SlotState available = AvailableState.INSTANCE;
            SlotState busy = BusyState.INSTANCE;

            assertTrue(available.isAvailable());
            assertFalse(available.isBusy());

            SlotState toBusy = available.markBusy();
            assertTrue(toBusy.isBusy());
            assertFalse(toBusy.isAvailable());

            assertTrue(busy.isBusy());
            assertFalse(busy.isAvailable());

            SlotState toAvailable = busy.markAvailable();
            assertTrue(toAvailable.isAvailable());
            assertFalse(toAvailable.isBusy());
        }
    }

    @Nested
    @DisplayName("SlotState Immutability and Thread Safety")
    class ImmutabilityTests {

        @Test
        @DisplayName("State transitions should return new state without modifying existing")
        void shouldReturnNewStateWithoutModifyingExisting() {
            SlotState original = AvailableState.INSTANCE;
            SlotState transitioned = original.markBusy();

            assertTrue(original.isAvailable());
            assertTrue(transitioned.isBusy());
        }

        @Test
        @DisplayName("Singleton instances should be reused across transitions")
        void shouldReuseSingletonInstancesAcrossTransitions() {
            SlotState available = AvailableState.INSTANCE;
            SlotState busy = available.markBusy();
            SlotState backToAvailable = busy.markAvailable();

            assertSame(AvailableState.INSTANCE, backToAvailable);
            assertSame(BusyState.INSTANCE, busy);
        }
    }

    @Nested
    @DisplayName("SlotState Exception Behavior")
    class ExceptionBehaviorTests {

        @Test
        @DisplayName("markAvailable on AvailableState should throw with descriptive message")
        void shouldThrowWithDescriptiveMessage() {
            SlotState state = AvailableState.INSTANCE;

            try {
                state.markAvailable();
                fail("Should have thrown InvalidSlotStateTransitionException");
            } catch (InvalidSlotStateTransitionException e) {
                assertTrue(e.getMessage().contains("Cannot"));
                assertTrue(e.getMessage().contains("AvailableState"));
            }
        }

        @Test
        @DisplayName("markBusy on BusyState should throw with descriptive message")
        void shouldThrowBusyMarkBusyWithDescriptiveMessage() {
            SlotState state = BusyState.INSTANCE;

            try {
                state.markBusy();
                fail("Should have thrown InvalidSlotStateTransitionException");
            } catch (InvalidSlotStateTransitionException e) {
                assertTrue(e.getMessage().contains("Cannot"));
                assertTrue(e.getMessage().contains("BusyState"));
            }
        }

        @Test
        @DisplayName("Exception should be instance of DomainException")
        void shouldThrowDomainException() {
            SlotState state = AvailableState.INSTANCE;

            assertThrows(
                    com.doodle.scheduler.application.domain.common.exception.DomainException.class,
                    state::markAvailable
            );
        }
    }
}
