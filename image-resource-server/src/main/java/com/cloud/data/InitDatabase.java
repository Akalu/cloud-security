package com.cloud.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;


/**
 * Initial data - for testing only
 * @author akaliutau
 */
@Component
@Slf4j
public class InitDatabase {

	@Bean
	CommandLineRunner init(final ImageRepository imageRepository) {
		return args -> {
			
			// copy all resources in /images to ./uploaded
			Image result = imageRepository.save(Image.builder().name("sample image").owner("me").build()).block();
			log.info("{}", result);

			log.info("======== sample records inserted ========");
			
		};
	}
	


}