package com.cloud.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.cloud.data.Image;
import com.cloud.data.ImageRepository;
import com.cloud.service.ImageService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of business logic
 * 
 * @author akaliutau
 */
@Service
@PreAuthorize("hasAnyRole('DB_USER', 'DB_ADMIN')")
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Mono<Image> findById(long imageId) {
        return imageRepository.findById(imageId);
    }

    @Override
    public Flux<Image> findAll() {
        return imageRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('DB_ADMIN')")
    public Mono<Void> create(Mono<Image> image) {
        return imageRepository.saveAll(image).then();
    }
    
    @Override
    @PreAuthorize("hasRole('DB_ADMIN')")
    public Mono<Void> update(Mono<Image> image) {
        return imageRepository.saveAll(image).then();
    }


    @Override
    @PreAuthorize("hasRole('DB_ADMIN')")
    public Mono<Void> deleteById(long imageIdentifier) {
        return imageRepository.deleteById(imageIdentifier).then();
    }
}