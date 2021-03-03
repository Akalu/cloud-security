package com.cloud.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.cloud.data.Image;
import com.cloud.service.impl.ImageServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@ActiveProfiles("test")
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@WebFluxTest
@WithMockUser
@DisplayName("Verify book api")
public class ApiIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;
    
    @MockBean 
    private ImageServiceImpl ImageServiceImpl;

    @MockBean 
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .apply(springSecurity()).configureClient()
                .baseUrl("http://localhost:8050")
                .filter(
                        documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                 .withResponseDefaults(prettyPrint()))
                .build();
        System.out.println("before:"+this.webTestClient);
    }

    @Test
    @DisplayName("functional test")
    public void redirectToLogin() {
        this.webTestClient.get().uri("/").exchange().expectStatus().is4xxClientError();
    }

  
    @Test
    @DisplayName("get list of Images")
    public void getAll() throws Exception {
        Long pid = 1L;

        given(ImageServiceImpl.findAll())
            .willReturn(
                Flux.just(
                		Image.builder().name("name").owner("admin").id(pid).build()));

        webTestClient
            .get()
            .uri("/images")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .json("[{\"id\":1,\"name\":\"name\",\"owner\":\"admin\"}]")
            .consumeWith(
                document(
                    "get-Images", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }
    
    @Test
    @DisplayName("get Image by id")
    public void getById() throws Exception {
        Long pid = 1L;

        given(ImageServiceImpl.findById(pid))
            .willReturn(
                Mono.just(
                		Image.builder().name("name").owner("admin").id(pid).build()));

        webTestClient
            .get()
            .uri("/images/"+ pid)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .json("{\"id\":1,\"name\":\"name\",\"owner\":\"admin\"}")
            .consumeWith(
                document(
                    "get-Images", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));

    }
    
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("create a Image record")
    @WithMockUser(roles = {"DB_ADMIN"})
    public void create() throws Exception {
        Long pid = 1L;
        
        given(ImageServiceImpl.create(any())).willAnswer(b -> Mono.empty());
        
        Image p = Image.builder().name("name").owner("admin").id(pid).build();

        webTestClient
            .mutateWith(csrf())
            .post()
            .uri("/images/")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(p))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody()
            .consumeWith(
                document(
                    "create-book",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())));
        
        ArgumentCaptor<Mono<Image>> bookArg = ArgumentCaptor.forClass(Mono.class);
        verify(ImageServiceImpl).create(bookArg.capture());

        assertThat(bookArg.getValue().block()).isNotNull().isEqualTo(p);

    }
    
    @Test
    @DisplayName("test of Image record deletion")
    void verifyAndDocumentDeleteBook() {

      Long pid = 1L;
      given(ImageServiceImpl.deleteById(pid)).willReturn(Mono.empty());

      webTestClient
          .mutateWith(csrf())
          .delete()
          .uri("/images/{pid}", pid)
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .consumeWith(
              document(
                  "delete-book",
                  preprocessRequest(prettyPrint()),
                  preprocessResponse(prettyPrint())));
    }

}
