package com.cloud.binding;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ResourceServiceAccessConfig {

	/**
	 * An experimental code, used to see what token is used in forwarded requests
	 * See the details on https://spring.io/blog/2018/03/06/using-spring-security-5-to-integrate-with-oauth-2-secured-services-such-as-facebook-and-github
	 * 
	 */
	@Bean
	@RequestScope
	public ResourceServer tokenExtractor(OAuth2AuthorizedClientService clientService) {
		
		// OAuth2AuthorizedClientService automatically configured as a bean in the Spring application context, 
		// so you’ll only need to inject it into wherever you’ll use it
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String accessToken = null;
		log.info("requesting {}", authentication);
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
		return new ResourceServer(accessToken);
	}
	

}
