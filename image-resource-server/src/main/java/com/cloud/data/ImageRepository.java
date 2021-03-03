package com.cloud.data;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {

}