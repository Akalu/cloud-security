package com.cloud.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.cloud.binding.LogOAuth2Details;
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

	/**
	 * OAuth2AuthorizedClient is not really needed here, I added it just to see the Spring oauth2 magic.
	 * To manually implement oauth2 functionality, see the guide on https://www.baeldung.com/spring-webclient-oauth2
	 * @param authorizedClient
	 * @return
	 */
	@GetMapping("/images")
	public Flux<Image> getAll(@RegisteredOAuth2AuthorizedClient  OAuth2AuthorizedClient authorizedClient) {
		log.info("requesting all images");
		log.info(LogOAuth2Details.getDetails(authorizedClient));
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
