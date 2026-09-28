ALTER TABLE swap_requests
ADD COLUMN offered_seat_id BIGINT;

ALTER TABLE swap_requests
ADD CONSTRAINT fk_swap_offered_seat
FOREIGN KEY (offered_seat_id)
REFERENCES seats(id);