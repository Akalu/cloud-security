package com.cloud.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.data.Image;
import com.cloud.service.ImageService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** 
 * REST api for image DB service
 * 
 * @author akaliutau
 */
@RestController
@RequestMapping
@Validated
public class ImageController {

    private static final String PATH_VARIABLE_IMAGE_ID = "imageId";

    private static final String PATH_IMAGE_ID = "{" + PATH_VARIABLE_IMAGE_ID + "}";

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images")
    public Flux<Image> getAll() {
        return imageService.findAll();
    }

    @GetMapping("/images/" + PATH_IMAGE_ID)
    public Mono<ResponseEntity<Image>> getImageById(@PathVariable(PATH_VARIABLE_IMAGE_ID) long imageId) {
        return imageService.findById(imageId).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/images")
    public Mono<Void> create(@RequestBody Mono<Image> imageResource) {
        return imageService.create(imageResource);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/images")
    public Mono<Void> update(@RequestBody Mono<Image> imageResource) {
        return imageService.update(imageResource);
    }

    @DeleteMapping("/images/" + PATH_IMAGE_ID)
    public Mono<Void> delete(@PathVariable(PATH_VARIABLE_IMAGE_ID) long imageId) {
        return imageService.deleteById(imageId);
    }
}