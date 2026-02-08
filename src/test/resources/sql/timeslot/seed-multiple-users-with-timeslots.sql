-- Use authenticated users for multi-user isolation tests
INSERT INTO users (id, username)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'authenticated-user'),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'test-user-2');

INSERT INTO time_slots (id, owner_id, start_time, end_time, duration_minutes, state)
VALUES
    ('111e4567-e89b-41d4-a716-446655440001', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2026-02-08 10:00:00+00', '2026-02-08 11:00:00+00', 60, 'AVAILABLE'),
    ('222e4567-e89b-41d4-a716-446655440002', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2026-02-08 14:00:00+00', '2026-02-08 15:00:00+00', 60, 'AVAILABLE'),
    ('333e4567-e89b-41d4-a716-446655440003', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '2026-02-09 09:00:00+00', '2026-02-09 10:00:00+00', 60, 'AVAILABLE');

