package com.rba.CreditCardService.kafka.consumer;

import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.repository.CardStatusRepository;
import com.rba.CreditCardService.repository.UserRepository;
import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.model.CreditCardStatusType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardStatusConsumer {

    private static final Logger ourLog = LoggerFactory.getLogger(CardStatusConsumer.class);

    private final UserRepository userRepository;

    private final CardStatusRepository cardStatusRepository;

    @KafkaListener(topics = "card-status-topic", groupId = "credit-card-inventory")
    public void handleStatusArrival(CardStatusUpdateMessage message) {
        ourLog.info("Received Card Status update: " + message);

        // Find the CardStatusType entity by ID
        CreditCardStatusType cardStatusType = cardStatusRepository.findById(message.getCardStatusTypeId())
                .orElseThrow(() -> new CreditCardUserException(ErrorEnum.INVALID_CARD_STATUS.code));

        //get user by oib and update status
        userRepository.findByOIB(message.getOib()).ifPresentOrElse(user -> {
            user.setStatus(cardStatusType);
            userRepository.save(user);
        }, () -> {
            ourLog.info("User not found with oib: " + message.getOib());
            throw new CreditCardUserException(ErrorEnum.USER_NOT_FOUND.code);
        });
    }

}
