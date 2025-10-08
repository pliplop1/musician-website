package com.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusicianWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicianWebsiteApplication.class, args);
	}

}
