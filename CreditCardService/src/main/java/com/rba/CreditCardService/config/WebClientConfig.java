package com.rba.CreditCardService.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

  private static final Logger ourLog = LoggerFactory.getLogger(WebClientConfig.class);

  @Value("${cardCreation.timeout}")
  public int timeoutCardCreation;

  @Value("${cardCreation.api.base-url}")
  private String cardCreationApiBaseUrl;

  @Bean
  @Qualifier("cardCreationWebClient")
  public WebClient webClientForCardCreation() {

    HttpClient httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutCardCreation)
            .responseTimeout(Duration.ofMillis(timeoutCardCreation))
            .doOnConnected(
                conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(timeoutCardCreation, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutCardCreation, TimeUnit.MILLISECONDS)));

    httpClient.warmup()
            .block();

    return WebClient.builder()
        .baseUrl(cardCreationApiBaseUrl)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
//        .filter(ExchangeFilterFunction.ofResponseProcessor(this::setResponseHeader))
        .build();
  }

  private Mono<ClientResponse> setResponseHeader(ClientResponse clientResponse) {
    return Mono.just(
        clientResponse
            .mutate()
            .headers(headers -> headers.remove(HttpHeaders.CONTENT_TYPE))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
            .build());
  }
}
