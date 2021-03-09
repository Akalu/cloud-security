package com.cloud.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class DBUserJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
	private static final String GROUPS_CLAIM = "groups";
	private static final String ROLE_PREFIX = "ROLE_";
	private static final String REALM_ACCESS = "realm_access";
	private static final String ROLES = "roles";

	private final ReactiveUserDetailsService userDetailsService;

	public DBUserJwtAuthenticationConverter(ReactiveUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
		log.info("authorities {}", authorities);
		log.info("jwt {}", jwt);
		return userDetailsService.findByUsername(jwt.getClaimAsString("email"))
				.map(u -> new UsernamePasswordAuthenticationToken(u, "n/a", authorities));
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		return this.getScopes(jwt).stream().map(authority -> ROLE_PREFIX + authority.toUpperCase())
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Collection<String> getScopes(Jwt jwt) {
		log.info("getClaims {}", jwt.getClaims());
		Object accessObj = jwt.getClaims().get(REALM_ACCESS);
		if (accessObj instanceof Map) {
			Object scopes = ((Map<String,Object>) accessObj).get(ROLES);
			if (scopes instanceof Collection) {
				return (Collection<String>) scopes;
			}
		}
		return Collections.emptyList();
	}

}
