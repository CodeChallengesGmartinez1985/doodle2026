CREATE INDEX idx_time_slots_owner_id ON time_slots(owner_id);
CREATE INDEX idx_time_slots_state ON time_slots(state);
CREATE INDEX idx_time_slots_start_time ON time_slots(start_time);
CREATE INDEX idx_time_slots_end_time ON time_slots(end_time);
CREATE INDEX idx_time_slots_owner_state ON time_slots(owner_id, state);
CREATE INDEX idx_time_slots_owner_start ON time_slots(owner_id, start_time);
