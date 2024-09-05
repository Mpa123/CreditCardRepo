package com.rba.CreditCardService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rba.CreditCardService.dto.CardStatusUpdateMessage;
import com.rba.CreditCardService.kafka.producer.CardStatusProducer;
import com.rba.CreditCardService.service.CardRequestService;
import com.rba.CreditCardService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class) // Enables Mockito in this test class
class CardStatusControllerTest {

    private MockMvc mockMvc;

    @Mock // Create a mock instance of the producer
    private CardStatusProducer cardStatusProducer;

    @InjectMocks // Inject the mocks into the controller
    private CardStatusController cardStatusController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Build MockMvc manually for testing the controller in isolation
        this.mockMvc = MockMvcBuilders.standaloneSetup(cardStatusController).build();
    }

    @Test
    void updateCardStatusSuccess() throws Exception {
        CardStatusUpdateMessage message = new CardStatusUpdateMessage();
        message.setCardStatusTypeId(1L);
        message.setOib("11111111111");

        doNothing().when(cardStatusProducer).sendStatusUpdate(message);

        mockMvc.perform(post("/api/v1/card-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(content().string("message published successfully..."));

        // Verify that the producer's method was called once
        verify(cardStatusProducer, times(1)).sendStatusUpdate(any(CardStatusUpdateMessage.class));
    }
}