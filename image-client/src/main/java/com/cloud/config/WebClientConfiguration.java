package com.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class WebClientConfiguration {

	@Bean
	WebClient webClient(ReactiveClientRegistrationRepository clientRegistrationRepository,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrationRepository, authorizedClientRepository);

		oauth.setDefaultOAuth2AuthorizedClient(true);
		oauth.setDefaultClientRegistrationId("keycloak");
		oauth.andThen(logRequest());
		
		return WebClient.builder()
				.filter(oauth)
				.filter(new LogHeadersExchangeFilter())
				.build();
	}
	
	ExchangeFilterFunction logRequest() {
		log.info("setup logRequest: done");
	    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
	    	log.info(clientRequest.toString());	    	
	        if (log.isInfoEnabled()) {
	            StringBuilder sb = new StringBuilder("Request: \n");
	            //append clientRequest method and url
	            clientRequest
	              .headers()
	              .forEach((name, values) -> values.forEach(value -> log.info(sb.toString())));
	        }
	        return Mono.just(clientRequest);
	    });
	}
}
