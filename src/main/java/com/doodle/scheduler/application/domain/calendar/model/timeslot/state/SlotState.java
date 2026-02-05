package com.doodle.scheduler.application.domain.calendar.model.timeslot.state;

import com.doodle.scheduler.application.domain.calendar.exception.InvalidSlotStateTransitionException;

public abstract class SlotState {
    /**
     * Public API - state transitions (return the next state)
     */
    public SlotState markBusy() {
        throw invalid("markBusy");
    }

    public SlotState markAvailable() {
        throw invalid("markAvailable");
    }

    /**
     * Public API - Public query methods (defaults)
     */
    public boolean isAvailable() {
        return false;
    }

    public boolean isBusy() {
        return false;
    }

    /**
     * Protected methods / constructors
     */
    protected InvalidSlotStateTransitionException invalid(String op) {
        return new InvalidSlotStateTransitionException("Cannot " + op + " from " + this.getClass().getSimpleName());
    }
}
