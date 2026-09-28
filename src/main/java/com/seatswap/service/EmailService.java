package com.seatswap.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSwapAcceptedEmail(
            String recipientEmail,
            String requesterName,
            String newCoach,
            Integer newSeat) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(recipientEmail);

        message.setSubject(
                "SeatSwap - Your Seat Swap Request Was Accepted"
        );

        message.setText(
                "Hi " + requesterName + ",\n\n" +

                        "Good news! Your seat swap request has been accepted.\n\n" +

                        "Your new seat:\n" +
                        "Coach: " + newCoach + "\n" +
                        "Seat: " + newSeat + "\n\n" +

                        "Your seat swap has been completed successfully.\n\n" +

                        "Regards,\n" +
                        "SeatSwap Team"
        );

        mailSender.send(message);
    }
}