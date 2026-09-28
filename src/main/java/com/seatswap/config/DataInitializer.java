package com.seatswap.config;

import com.seatswap.entity.*;
import com.seatswap.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            TrainRepository trainRepository,
            JourneyRepository journeyRepository,
            CoachRepository coachRepository,
            SeatRepository seatRepository,
            PassengerRepository passengerRepository,
            BookingRepository bookingRepository) {

        return args -> {

            // -------------------------------------------------
            // 1. Find or create Train
            // -------------------------------------------------

            Train train = trainRepository.findByTrainNumber("12951")
                    .orElseGet(() -> {
                        Train newTrain = new Train();
                        newTrain.setTrainNumber("12951");
                        newTrain.setTrainName("Mumbai Rajdhani");
                        return trainRepository.save(newTrain);
                    });


            // -------------------------------------------------
            // 2. Find existing Journey or create one
            // -------------------------------------------------

            Journey journey = journeyRepository.findAll()
                    .stream()
                    .filter(j ->
                            j.getTrain().getId().equals(train.getId()))
                    .findFirst()
                    .orElseGet(() -> {

                        Journey newJourney = new Journey();

                        newJourney.setTrain(train);

                        newJourney.setDepartureDateTime(
                                LocalDateTime.of(2026, 10, 10, 17, 0)
                        );

                        newJourney.setArrivalDateTime(
                                LocalDateTime.of(2026, 10, 11, 8, 35)
                        );

                        return journeyRepository.save(newJourney);
                    });


            // -------------------------------------------------
            // 3. Find existing Coach or create one
            // -------------------------------------------------

            Coach coach = coachRepository.findAll()
                    .stream()
                    .filter(c ->
                            c.getJourney().getId().equals(journey.getId())
                                    && c.getCoachNumber().equals("B2"))
                    .findFirst()
                    .orElseGet(() -> {

                        Coach newCoach = new Coach();

                        newCoach.setJourney(journey);
                        newCoach.setCoachNumber("B2");
                        newCoach.setCoachType("3A");

                        return coachRepository.save(newCoach);
                    });


            // -------------------------------------------------
            // 4. Find/create Seat 31
            // -------------------------------------------------

            Seat seat31 = seatRepository.findAll()
                    .stream()
                    .filter(s ->
                            s.getCoach().getId().equals(coach.getId())
                                    && s.getSeatNumber().equals(31))
                    .findFirst()
                    .orElseGet(() -> {

                        Seat newSeat = new Seat();

                        newSeat.setCoach(coach);
                        newSeat.setSeatNumber(31);
                        newSeat.setBerthType(BerthType.LOWER);

                        return seatRepository.save(newSeat);
                    });


            // -------------------------------------------------
            // 5. Find/create Seat 32
            // -------------------------------------------------

            Seat seat32 = seatRepository.findAll()
                    .stream()
                    .filter(s ->
                            s.getCoach().getId().equals(coach.getId())
                                    && s.getSeatNumber().equals(32))
                    .findFirst()
                    .orElseGet(() -> {

                        Seat newSeat = new Seat();

                        newSeat.setCoach(coach);
                        newSeat.setSeatNumber(32);
                        newSeat.setBerthType(BerthType.UPPER);

                        return seatRepository.save(newSeat);
                    });


            // -------------------------------------------------
            // 6. Find/create Seat 33
            // -------------------------------------------------

            Seat seat33 = seatRepository.findAll()
                    .stream()
                    .filter(s ->
                            s.getCoach().getId().equals(coach.getId())
                                    && s.getSeatNumber().equals(33))
                    .findFirst()
                    .orElseGet(() -> {

                        Seat newSeat = new Seat();

                        newSeat.setCoach(coach);
                        newSeat.setSeatNumber(33);
                        newSeat.setBerthType(BerthType.LOWER);

                        return seatRepository.save(newSeat);
                    });


            // -------------------------------------------------
            // 7. Find/create Seat 34
            // -------------------------------------------------

            Seat seat34 = seatRepository.findAll()
                    .stream()
                    .filter(s ->
                            s.getCoach().getId().equals(coach.getId())
                                    && s.getSeatNumber().equals(34))
                    .findFirst()
                    .orElseGet(() -> {

                        Seat newSeat = new Seat();

                        newSeat.setCoach(coach);
                        newSeat.setSeatNumber(34);
                        newSeat.setBerthType(BerthType.UPPER);

                        return seatRepository.save(newSeat);
                    });


            // -------------------------------------------------
            // 8. Find/create Rahul
            // -------------------------------------------------

            Passenger rahul = passengerRepository
                    .findByEmail("rahul@example.com")
                    .orElseGet(() -> {

                        Passenger passenger = new Passenger();

                        passenger.setName("Rahul");
                        passenger.setEmail("rahul@example.com");

                        return passengerRepository.save(passenger);
                    });


            // -------------------------------------------------
            // 9. Find/create Amit
            // -------------------------------------------------

            Passenger amit = passengerRepository
                    .findByEmail("amit@example.com")
                    .orElseGet(() -> {

                        Passenger passenger = new Passenger();

                        passenger.setName("Amit");
                        passenger.setEmail("amit@example.com");

                        return passengerRepository.save(passenger);
                    });


            // -------------------------------------------------
            // 10. Find/create Priya
            // -------------------------------------------------

            Passenger priya = passengerRepository
                    .findByEmail("priya@example.com")
                    .orElseGet(() -> {

                        Passenger passenger = new Passenger();

                        passenger.setName("Priya");
                        passenger.setEmail("priya@example.com");

                        return passengerRepository.save(passenger);
                    });


            // -------------------------------------------------
            // 11. Find/create Neha
            // -------------------------------------------------

            Passenger neha = passengerRepository
                    .findByEmail("neha@example.com")
                    .orElseGet(() -> {

                        Passenger passenger = new Passenger();

                        passenger.setName("Neha");
                        passenger.setEmail("neha@example.com");

                        return passengerRepository.save(passenger);
                    });


            // -------------------------------------------------
            // 12. Find/create Rahul's booking
            // -------------------------------------------------

            if (bookingRepository.findByPnr("4512789632")
                    .stream()
                    .noneMatch(b ->
                            b.getPassenger().getId()
                                    .equals(rahul.getId()))) {

                Booking booking = new Booking();

                booking.setPnr("4512789632");
                booking.setPassenger(rahul);
                booking.setJourney(journey);
                booking.setSeat(seat31);
                booking.setStatus(BookingStatus.CONFIRMED);

                bookingRepository.save(booking);
            }


            // -------------------------------------------------
            // 13. Find/create Priya's booking
            // -------------------------------------------------

            if (bookingRepository.findByPnr("4512789632")
                    .stream()
                    .noneMatch(b ->
                            b.getPassenger().getId()
                                    .equals(priya.getId()))) {

                Booking booking = new Booking();

                booking.setPnr("4512789632");
                booking.setPassenger(priya);
                booking.setJourney(journey);
                booking.setSeat(seat33);
                booking.setStatus(BookingStatus.CONFIRMED);

                bookingRepository.save(booking);
            }


            // -------------------------------------------------
            // 14. Find/create Amit's booking
            // -------------------------------------------------

            if (bookingRepository.findByPnr("5678912340")
                    .stream()
                    .noneMatch(b ->
                            b.getPassenger().getId()
                                    .equals(amit.getId()))) {

                Booking booking = new Booking();

                booking.setPnr("5678912340");
                booking.setPassenger(amit);
                booking.setJourney(journey);
                booking.setSeat(seat32);
                booking.setStatus(BookingStatus.CONFIRMED);

                bookingRepository.save(booking);
            }


            // -------------------------------------------------
            // 15. Find/create Neha's booking
            // -------------------------------------------------

            if (bookingRepository.findByPnr("5678912340")
                    .stream()
                    .noneMatch(b ->
                            b.getPassenger().getId()
                                    .equals(neha.getId()))) {

                Booking booking = new Booking();

                booking.setPnr("5678912340");
                booking.setPassenger(neha);
                booking.setJourney(journey);
                booking.setSeat(seat34);
                booking.setStatus(BookingStatus.CONFIRMED);

                bookingRepository.save(booking);
            }


            System.out.println("Test data initialized successfully.");
        };
    }
}