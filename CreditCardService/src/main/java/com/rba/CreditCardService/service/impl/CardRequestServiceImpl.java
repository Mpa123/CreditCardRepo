package com.rba.CreditCardService.service.impl;

import com.rba.CreditCardService.dto.NewCardRequest;
import com.rba.CreditCardService.exceptions.CreditCardUserException;
import com.rba.CreditCardService.exceptions.ErrorEnum;
import com.rba.CreditCardService.model.User;
import com.rba.CreditCardService.service.CardRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CardRequestServiceImpl implements CardRequestService {

    private static final Logger ourLog = LoggerFactory.getLogger(CardRequestServiceImpl.class);

    private final WebClient webClient;

    public CardRequestServiceImpl(@Qualifier("cardCreationWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    private static final String CARD_REQUEST_URL = "/api/v1/card-request";


    @Override
    public void forwardClientDetails(User user) {
        NewCardRequest newCardRequest = new NewCardRequest();
        newCardRequest.setFirstName(user.getFirstName());
        newCardRequest.setLastName(user.getLastName());
        newCardRequest.setStatus(String.valueOf(user.getStatus()));
        newCardRequest.setOib(user.getOIB());

        ourLog.info("New Card Request: " + newCardRequest);

        webClient.post()
                .uri(CARD_REQUEST_URL)
                .body(Mono.just(newCardRequest), NewCardRequest.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, clientResponse ->
                        handleError(clientResponse, ErrorEnum.CARD_CREATION_BAD_REQUEST)
                )
                .onStatus(HttpStatus.UNAUTHORIZED::equals, clientResponse ->
                        handleError(clientResponse, ErrorEnum.CARD_CREATION_UNAUTHORIZED)
                )
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        handleError(clientResponse, ErrorEnum.CARD_CREATION_NOT_FOUND)
                )
                .onStatus(this::is5xxServerError, clientResponse ->
                        handleError(clientResponse, ErrorEnum.CARD_CREATION_INTERNAL_SERVER_ERROR)
                )
                .bodyToMono(String.class)
                .doOnSuccess(response -> ourLog.info("New card request successfully created: " + response))
                .block();
    }

    private boolean is5xxServerError(HttpStatusCode statusCode) {
        return statusCode.is5xxServerError();
    }

    private Mono<? extends Throwable> handleError(ClientResponse clientResponse, ErrorEnum errorEnum) {
        return clientResponse.bodyToMono(ErrorResponse.class)
                .flatMap(errorResponse -> {
                    ourLog.error("Error: " + errorResponse.getStatusCode() + " - " + errorResponse.getDetailMessageCode());
                    return Mono.error(new CreditCardUserException(errorEnum.code));
                });
    }
}
