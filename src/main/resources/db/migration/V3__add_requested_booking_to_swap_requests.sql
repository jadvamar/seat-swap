ALTER TABLE swap_requests
ADD COLUMN requested_booking_id BIGINT;

ALTER TABLE swap_requests
ADD CONSTRAINT fk_swap_requested_booking
FOREIGN KEY (requested_booking_id)
REFERENCES bookings(id);