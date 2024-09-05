package com.rba.CreditCardService.kafka.producer;

import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardStatusProducerTest {

    @InjectMocks
    private CardStatusProducer cardStatusProducer;
    @Mock
    private KafkaTemplate<String, CardStatusUpdateMessage> kafkaTemplate;

    private CardStatusUpdateMessage message;

    @BeforeEach
    void setUp() {
        message = new CardStatusUpdateMessage();
    }

    @Test
    void testSendStatusUpdate_Success() {
        // successful send
        CompletableFuture<SendResult<String, CardStatusUpdateMessage>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(String.class), any(CardStatusUpdateMessage.class))).thenReturn(future);

        cardStatusProducer.sendStatusUpdate(message);

        //
        verify(kafkaTemplate, times(1)).send("card-status-topic", message);
    }

    @Test
    void testSendStatusUpdate_Failure() throws InterruptedException {
        CompletableFuture<SendResult<String, CardStatusUpdateMessage>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka send error"));
        when(kafkaTemplate.send(any(String.class), any(CardStatusUpdateMessage.class))).thenReturn(future);

        // using CountDownLatch to ensure async completion because asynchronous call and so exceptions dont propagate immediately
        CountDownLatch latch = new CountDownLatch(1);

        try {
            cardStatusProducer.sendStatusUpdate(message);
        } catch (Exception e) {
            // The exception won't be caught here as it's in the CompletableFuture
        }

        // waiting CompletableFuture to complete
        latch.await(1, TimeUnit.SECONDS);

        // No exception will be thrown directly, but we can check if the logs were hit
        //ERROR com.rba.CreditCardService.kafka.producer.CardStatusProducer -- Unable to send
        // message=[CardStatusUpdateMessage{oib='null', cardStatus='null'}] due to : Kafka send error
        verify(kafkaTemplate, times(1)).send("card-status-topic", message);
    }
}