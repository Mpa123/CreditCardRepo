package com.rba.CreditCardService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.exceptions.WebRestControllerAdvice;
import com.rba.CreditCardService.kafka.producer.CardStatusProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardStatusController.class) // Loads only the controller and necessary Spring MVC components
class CardStatusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardStatusProducer cardStatusProducer;

    @MockBean
    private WebRestControllerAdvice webRestControllerAdvice;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void updateCardStatusSuccess() throws Exception {
        CardStatusUpdateMessage message = new CardStatusUpdateMessage();
        message.setCardStatusTypeId(1L);
        message.setOib("11111111111");

//        doNothing().when(cardStatusProducer).sendStatusUpdate(message);

        // Perform the test, simulating an actual HTTP request
        mockMvc.perform(post("/api/v1/card-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(content().string("message published successfully..."));

        // Verify that the producer's method was called once
        verify(cardStatusProducer, times(1)).sendStatusUpdate(any(CardStatusUpdateMessage.class));
    }

//    @Test
//    public void testUpdateCardStatus_error() throws Exception {
//        CardStatusUpdateMessage message = new CardStatusUpdateMessage();
//        message.setCardStatusTypeId(1L);
//        message.setOib("11111111111");
//
//        doThrow(new RuntimeException("Kafka error")).when(cardStatusProducer).sendStatusUpdate(any(CardStatusUpdateMessage.class));
//
//        mockMvc.perform(post("/api/v1/card-status")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(message)))
//                .andExpect(status().isInternalServerError());
//    }
}