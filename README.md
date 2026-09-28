# SeatSwap

SeatSwap is a train seat-swapping platform that allows passengers to request and exchange seats with other passengers traveling on the same journey.

The project solves a real-world problem where passengers traveling together may be assigned different seats or coaches and want to exchange seats after booking.

The main engineering challenge is ensuring that seat swaps remain **safe and consistent when multiple passengers perform requests or accept swaps at the same time**.

SeatSwap therefore focuses on:

* Secure passenger authentication and authorization
* Validating eligible seat swaps
* Preventing conflicting or duplicate swap requests
* Safely exchanging seats using database transactions and concurrency control
* Ensuring stale requests cannot corrupt booking data
* Notifying passengers after a successful swap

The goal is to build a backend system where a seat exchange is treated as a **transactionally consistent operation**, rather than simply updating two database records.
