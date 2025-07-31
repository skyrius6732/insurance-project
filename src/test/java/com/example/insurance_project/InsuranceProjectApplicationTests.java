package com.example.insurance_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.kafka.streams.auto-startup=false")
class InsuranceProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}
