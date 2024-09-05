package com.rba.CreditCardService.kafka.producer;

import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@AllArgsConstructor
public class CardStatusProducer {

    private static final Logger ourLog = LoggerFactory.getLogger(CardStatusProducer.class);


    private final KafkaTemplate<String, CardStatusUpdateMessage> kafkaTemplate;

    /**
     * Handling Asynchronously: we send the message to Kafka topic asynchronously and log any
     * errors in the process.
     * @param msg
     */
    public void sendStatusUpdate(CardStatusUpdateMessage msg) {
        CompletableFuture<SendResult<String, CardStatusUpdateMessage>> future = kafkaTemplate.send("card-status-topic", msg);
        future.whenComplete((result, exception) -> {
            if (exception == null) {
                ourLog.debug("Sent message=[" + msg +
                        "] with offset=[" + result.getRecordMetadata().offset() +
                        "]");
            } else {
                ourLog.error("Unable to send message=[" +
                        msg + "] due to : " + exception.getMessage());
                throw new CreditCardUserException(ErrorEnum.UNEXPECTED_ERROR.code);
            }
        });
    }
}
