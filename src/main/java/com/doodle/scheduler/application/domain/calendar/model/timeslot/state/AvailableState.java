package com.doodle.scheduler.application.domain.calendar.model.timeslot.state;

public final class AvailableState extends SlotState {
    private AvailableState() {}

    public static final AvailableState INSTANCE = new AvailableState();

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public SlotState markBusy() {
        return BusyState.INSTANCE;
    }
}
