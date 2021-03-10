package com.cloud.binding;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public class LogOAuth2Details {

	public static String getDetails(OAuth2AuthorizedClient authorizedClient) {
		StringBuilder sb = new StringBuilder();
		if (authorizedClient != null) {
			sb.append("\n");
			sb.append("using oauth2 with principal " + authorizedClient.getPrincipalName() + "\n");
			sb.append("client id: " + authorizedClient.getClientRegistration().getClientId() + "\n");
			sb.append("client name: " + authorizedClient.getClientRegistration().getClientName() + "\n");
			sb.append("redirect uri: " + authorizedClient.getClientRegistration().getRedirectUri() + "\n");
		}
		return sb.toString();
	}
}
