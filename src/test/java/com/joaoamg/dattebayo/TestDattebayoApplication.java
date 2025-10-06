package com.joaoamg.dattebayo;

import org.springframework.boot.SpringApplication;

public class TestDattebayoApplication {

	public static void main(String[] args) {
		SpringApplication.from(DattebayoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
