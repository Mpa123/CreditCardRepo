package com.rba.CreditCardService.controller;

import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.kafka.producer.CardStatusProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Controller for handling credit card status updates.
 * This controller is responsible for receiving card status update requests
 * and sending them to the appropriate Kafka topic via the CardStatusProducer.
 */
@RestController
@RequestMapping("/api/v1/card-status")
public class CardStatusController {

    private static final Logger ourLog = LoggerFactory.getLogger(CardStatusController.class);
    @Autowired
    private CardStatusProducer cardStatusProducer;

    /**
     * Updates the card status by sending the provided status update message
     * to the Kafka topic.
     *
     * @param msg The card status update message containing details about the status change and OIB of the user.
     *            This message is expected to be in JSON format and must match the
     *            structure of the {@link CardStatusUpdateMessage} class.
     *            Handling Asynchronously: success message is returned immediately at controller level but
     *            errors are logged separately without affecting the controller response.
     * @return A ResponseEntity containing a success message and an HTTP status code of 200 (OK).
     * @response 200 The message was successfully sent to the Kafka topic.
     */
    @PostMapping
    public ResponseEntity<Object> updateCardStatus(@RequestBody CardStatusUpdateMessage msg) {
        ourLog.info("Received card status update: " + msg);
        cardStatusProducer.sendStatusUpdate(msg);
        return new ResponseEntity<>("message published successfully...", HttpStatus.OK);
    }
}
