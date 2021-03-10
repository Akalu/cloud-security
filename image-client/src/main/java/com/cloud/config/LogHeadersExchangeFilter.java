package com.cloud.config;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class LogHeadersExchangeFilter implements ExchangeFilterFunction {
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    	log.info("url: {}", request.url());
    	log.info("headers: {}", request.headers().keySet());
    	log.info("cookies: {}", request.cookies().keySet());
        return next.exchange(request);
    }
}
