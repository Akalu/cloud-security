package com.cloud.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cloud.data.Image;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ImageController {

	private final WebClient webClient;

	@Value("${resource.server}")
	private String resourceServer;

	public ImageController(WebClient webClient) {
		this.webClient = webClient;
	}

	@GetMapping("/images")
	public Flux<Image> getAll() {
		log.info("requesting all images");
		return webClient
				.get()
				.uri(resourceServer + "/images")
				.retrieve()
				.onStatus(s -> s.equals(HttpStatus.UNAUTHORIZED),
						cr -> Mono.just(new BadCredentialsException("Not authenticated")))
				.onStatus(s -> s.equals(HttpStatus.FORBIDDEN),
						cr -> Mono.just(new AccessDeniedException("Not authorized")))
				.onStatus(HttpStatus::is4xxClientError,
						cr -> Mono.just(new IllegalArgumentException(cr.statusCode().getReasonPhrase())))
				.onStatus(HttpStatus::is5xxServerError,
						cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())))
				.bodyToFlux(Image.class);
	}

}
