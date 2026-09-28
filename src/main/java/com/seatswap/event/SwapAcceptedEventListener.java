package com.seatswap.event;

import com.seatswap.service.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SwapAcceptedEventListener {

    private final EmailService emailService;

    public SwapAcceptedEventListener(
            EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleSwapAccepted(
            SwapAcceptedEvent event) {

        emailService.sendSwapAcceptedEmail(
                event.getRequesterEmail(),
                event.getRequesterName(),
                event.getNewCoach(),
                event.getNewSeat()
        );
    }
}