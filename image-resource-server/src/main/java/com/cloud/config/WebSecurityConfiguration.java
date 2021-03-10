package com.cloud.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.cloud.model.Role;

/**
 * Configuration for local testing and R&D
 * For production use there will be needed some changes
 *  
 * @author akaliutau
 *
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
//@EnableSpringWebSession
public class WebSecurityConfiguration {
	

	private final ReactiveUserDetailsService userDetailsService;
	
	@Autowired
	public WebSecurityConfiguration(ReactiveUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain2(ServerHttpSecurity http) {
    	
    	// NoOpServerSecurityContextRepository is used to for stateless sessions so no session or state is persisted between requests.
        // The client must send the Authorization header with every request.
        NoOpServerSecurityContextRepository sessionConfig = NoOpServerSecurityContextRepository.getInstance();

        http
        .securityContextRepository(sessionConfig)
          .csrf().disable()
          .httpBasic()// supports also BASIC auth - must be turned off in prod
          .and().cors()
          .and().authorizeExchange()
        		.matchers(EndpointRequest.toAnyEndpoint()).hasAnyRole(Role.DB_USER.name(), Role.DB_ADMIN.name())
        		.pathMatchers("/images/**")
                .hasAnyRole(Role.DB_ADMIN.name(), Role.DB_USER.name())// potentially here can be complex logic
        		.anyExchange().authenticated()
        		.and()
        		.oauth2ResourceServer()
                .jwt() 
                .jwtAuthenticationConverter(dbUserJwtAuthenticationConverter());
                
        return http.build();
    }

    @Profile("test")
    @Bean("corsConfigurationSource")
    public CorsConfigurationSource corsConfigurationSourceDev() {
        return commonCors("*");
    }

    private CorsConfigurationSource commonCors(String... origin) {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(origin));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));

        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must
        // not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);

        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "Accept", "Origin", "X-Requested-With", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Beans declarations
     */
    
    /**
     * This bean declares an in-memory databases for user with username=resource_client and roles DB_USER, DB_ADMIN
     * Note, the password is specified - the BASIC auth will still work, but the auth must be performed via oath2 authorization server
     * @return
     */
    @Bean
    @Primary
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("resource_client").password("{noop}123").roles(Role.DB_USER.name(), Role.DB_ADMIN.name()).build();
        return new MapReactiveUserDetailsService(user);
    }
    
    /**
     * This bean declares an in-memory databases for user with username=user and password=123 and roles DB_USER, DB_ADMIN
     * @return
     */
    @Bean
    //@Primary
    public MapReactiveUserDetailsService userDetailsServiceOffLine() {
        UserDetails user = User.withUsername("user").password("{noop}123").roles(Role.DB_USER.name(), Role.DB_ADMIN.name()).build();
        return new MapReactiveUserDetailsService(user);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public DBUserJwtAuthenticationConverter dbUserJwtAuthenticationConverter() {
      return new DBUserJwtAuthenticationConverter(userDetailsService);
    }

}