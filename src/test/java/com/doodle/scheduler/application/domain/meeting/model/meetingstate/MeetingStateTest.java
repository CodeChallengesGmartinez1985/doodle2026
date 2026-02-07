package com.doodle.scheduler.application.domain.meeting.model.meetingstate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MeetingState - State Pattern")
class MeetingStateTest {

    @Nested
    @DisplayName("ScheduledState Singleton")
    class ScheduledStateTests {

        @Test
        @DisplayName("Should be singleton instance")
        void shouldBeSingletonInstance() {
            ScheduledState state1 = ScheduledState.INSTANCE;
            ScheduledState state2 = ScheduledState.INSTANCE;

            assertSame(state1, state2);
        }

        @Test
        @DisplayName("Should report isScheduled as true")
        void shouldReportIsScheduledTrue() {
            MeetingState state = ScheduledState.INSTANCE;

            assertTrue(state.isScheduled());
        }

        @Test
        @DisplayName("Should have meaningful string representation")
        void shouldHaveMeaningfulStringRepresentation() {
            MeetingState state = ScheduledState.INSTANCE;

            String toString = state.toString();
            assertNotNull(toString);
            assertFalse(toString.isEmpty());
        }

        @Test
        @DisplayName("Should be instance of MeetingState")
        void shouldBeInstanceOfMeetingState() {
            MeetingState state = ScheduledState.INSTANCE;

            assertTrue(state instanceof MeetingState);
            assertTrue(state instanceof ScheduledState);
        }

        @Test
        @DisplayName("Should return correct state string")
        void shouldReturnCorrectStateString() {
            MeetingState state = ScheduledState.INSTANCE;

            assertEquals("SCHEDULED", state.getStateString());
        }

        @Test
        @DisplayName("Should return consistent state string across multiple calls")
        void shouldReturnConsistentStateString() {
            MeetingState state = ScheduledState.INSTANCE;

            String state1 = state.getStateString();
            String state2 = state.getStateString();
            String state3 = state.getStateString();

            assertEquals(state1, state2);
            assertEquals(state2, state3);
            assertEquals("SCHEDULED", state1);
        }
    }

    @Nested
    @DisplayName("MeetingState Immutability and Consistency")
    class ImmutabilityTests {

        @Test
        @DisplayName("State should be immutable across multiple calls")
        void shouldBeImmutableAcrossMultipleCalls() {
            MeetingState state = ScheduledState.INSTANCE;

            boolean result1 = state.isScheduled();
            boolean result2 = state.isScheduled();
            boolean result3 = state.isScheduled();

            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);
        }

        @Test
        @DisplayName("Should maintain identity and semantics across references")
        void shouldMaintainIdentityAndSemanticsAcrossReferences() {
            MeetingState state1 = ScheduledState.INSTANCE;
            MeetingState state2 = ScheduledState.INSTANCE;

            assertTrue(state1.isScheduled());
            assertTrue(state2.isScheduled());
            assertSame(state1, state2);
            assertSame(ScheduledState.INSTANCE, state1);
        }
    }
}


