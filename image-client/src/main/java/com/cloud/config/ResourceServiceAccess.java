package com.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import com.cloud.data.Token;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ResourceServiceAccess {

	@Bean
	@RequestScope
	public Token tokenExtractor(OAuth2AuthorizedClientService clientService) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String accessToken = null;
		if (authentication.getClass().isAssignableFrom(OAuth2AuthenticationToken.class)) {
			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
			String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();
			log.info("clientRegistrationId={}",clientRegistrationId);

			if (clientRegistrationId.equals("resource-client-id")) {
				OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(clientRegistrationId,
						oauthToken.getName());
				accessToken = client.getAccessToken().getTokenValue();

				log.info(accessToken);
			}
		}
		return new Token(accessToken, null);
	}

}
