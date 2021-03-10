package com.cloud.binding;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

public abstract class ReactiveApiBinding {
	protected WebClient webClient;

	public ReactiveApiBinding(String accessToken) {
		Builder builder = WebClient.builder();
		if (accessToken != null) {
			builder.defaultHeader("Authorization", "Bearer " + accessToken);
		} else {
			builder.exchangeFunction(request -> {
				throw new IllegalStateException("Can't access the API without an access token");
			});
		}
		this.webClient = builder.build();
	}
}
