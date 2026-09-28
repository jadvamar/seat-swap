ALTER TABLE bookings
DROP CONSTRAINT IF EXISTS uk_journey_seat;

ALTER TABLE bookings
ADD CONSTRAINT uk_journey_seat
UNIQUE (journey_id, seat_id)
DEFERRABLE INITIALLY DEFERRED;