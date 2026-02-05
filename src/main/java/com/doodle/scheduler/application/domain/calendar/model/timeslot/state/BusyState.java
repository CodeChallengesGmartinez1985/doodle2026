package com.doodle.scheduler.application.domain.calendar.model.timeslot.state;

public final class BusyState extends SlotState {
    private BusyState() {}

    public static final BusyState INSTANCE = new BusyState();

    @Override
    public boolean isBusy() {
        return true;
    }

    @Override
    public SlotState markAvailable() {
        return AvailableState.INSTANCE;
    }
}
