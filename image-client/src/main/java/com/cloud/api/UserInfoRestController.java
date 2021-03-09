package com.cloud.api;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class UserInfoRestController {

	@GetMapping("/userinfo")
	Mono<Map<String, Object>> userInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
		return Mono.just(oauth2User.getAttributes());
	}
}
