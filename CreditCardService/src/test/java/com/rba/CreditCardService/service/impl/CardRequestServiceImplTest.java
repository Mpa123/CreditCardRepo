package com.rba.CreditCardService.service.impl;

import com.rba.CreditCardService.dto.ErrorResponse;
import com.rba.CreditCardService.dto.NewCardRequest;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.model.CreditCardStatusType;
import com.rba.CreditCardService.model.User;
import com.rba.CreditCardService.service.CardRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardRequestServiceImplTest {

    @Mock
    WebClient webClient;

    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    WebClient.RequestBodySpec requestBodySpec;

    @Mock
    WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CardRequestServiceImpl cardRequestService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the WebClient behavior
        when(requestHeadersSpec.header(any(),any())).thenReturn(requestHeadersSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(NewCardRequest.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testForwardClientDetails_Success() {
        // Prepare
        User user = User.builder()
                .firstName("Marko")
                .lastName("Markić")
                .OIB("12345678912")
                .status(new CreditCardStatusType(1L, "Approved"))
                .build();

        NewCardRequest newCardRequest = new NewCardRequest();
        newCardRequest.setFirstName("John");
        newCardRequest.setLastName("Doe");
        newCardRequest.setStatus("Approved");
        newCardRequest.setOib("12345678912");

        // Mock successful response
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

        // Action
        cardRequestService.forwardClientDetails(user);

        // Verify
        verify(requestBodySpec).bodyValue(newCardRequest);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }
}