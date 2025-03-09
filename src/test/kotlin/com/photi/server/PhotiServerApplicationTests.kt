package com.photi.server

import com.photi.server.framework.TestContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestContainerInitializer::class])
class PhotiServerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
