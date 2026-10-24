package com.nsu.medbook_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Boots the full context against the in-memory H2 datasource from
 * application-test.yml so tests never depend on a local MySQL server.
 */
@SpringBootTest
@ActiveProfiles("test")
class MedbookBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
