package com.alloon.alloonserver

import com.alloon.alloonserver.framework.TestContainerInitializer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [TestContainerInitializer::class])
class AlloonServerApplicationTests {

	@Test
	fun contextLoads() {
	}

}
