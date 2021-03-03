package com.cloud.service;

import com.cloud.data.Image;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageService {

    Mono<Image> findById(long imageId);

    Flux<Image> findAll();

    Mono<Void> create(Mono<Image> image);

    Mono<Void> update(Mono<Image> image);

    Mono<Void> deleteById(long imageIdentifier);

}