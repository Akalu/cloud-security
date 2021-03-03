package com.cloud.service;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.cloud.ImageResourceServerApplication;
import com.cloud.data.Image;
import com.cloud.data.ImageRepository;
import com.cloud.service.ImageService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("Verify that book service")
@SpringJUnitConfig(ImageResourceServerApplication.class)
public class ImageServiceAuthTest {
    
    @Autowired
    private ImageService personService;

    @MockBean
    private ImageRepository personRepository;

    @MockBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @DisplayName("grants access to create a record in DB for role 'DB_ADMIN'")
    @Test
    @WithMockUser(roles = "DB_ADMIN")
    void verifyCreateAccessIsGranted() {
        
        when(personRepository.saveAll(Mockito.<Mono<Image>>any())).thenReturn(Flux.just(new Image()));
        
        StepVerifier.create(personService.create(Mono.just(new Image(1L, "test1", "alice")))).verifyComplete();
    }
    
    @DisplayName("deny access to create a record in DB for role 'DB_USER'")
    @Test
    @WithMockUser(roles = "DB_USER")
    void verifyDenyAccessIsGranted() {
        
        when(personRepository.saveAll(Mockito.<Mono<Image>>any())).thenReturn(Flux.just(new Image()));
        
        StepVerifier.create(personService.create(Mono.just(new Image(1L, "test1", "alice")))).expectError();
    }
}
