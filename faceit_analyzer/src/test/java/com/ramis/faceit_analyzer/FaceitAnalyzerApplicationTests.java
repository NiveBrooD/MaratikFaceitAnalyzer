package com.ramis.faceit_analyzer;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@SpringBootTest
class FaceitAnalyzerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() {
		log.info(String.valueOf(Instant.parse(LocalDate.now().toString())));
	}
}
