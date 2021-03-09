package com.cloud.config;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.cloud.model.Role;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Alternative implementation of ReactiveUserDetailsService, not really used in this project
 * @author akaliutau
 *
 */
@Slf4j
@Service
public class DBReactiveUserDetailsService implements ReactiveUserDetailsService {

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		log.info("Finding user for user name {}", username);
		UserDetails user = User.withUsername("user").password("{noop}123")
				.roles(Role.DB_USER.name(), Role.DB_ADMIN.name()).build();

		return Mono.just(user);
	}
}
