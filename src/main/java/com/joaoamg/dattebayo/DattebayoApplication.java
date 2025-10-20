package com.joaoamg.dattebayo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DattebayoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DattebayoApplication.class, args);
	}
	
}
