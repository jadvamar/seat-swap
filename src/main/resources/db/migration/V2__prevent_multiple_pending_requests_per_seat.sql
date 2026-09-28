CREATE UNIQUE INDEX ux_pending_swap_requested_seat
ON swap_requests (requested_seat_id)
WHERE status = 'PENDING';

CREATE UNIQUE INDEX ux_pending_swap_offered_booking
ON swap_requests (offered_booking_id)
WHERE status = 'PENDING';