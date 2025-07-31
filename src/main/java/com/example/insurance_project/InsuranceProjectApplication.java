package com.example.insurance_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InsuranceProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceProjectApplication.class, args);
	}

}
