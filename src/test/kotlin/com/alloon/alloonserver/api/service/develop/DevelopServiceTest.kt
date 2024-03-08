package com.alloon.alloonserver.api.service.develop

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DevelopServiceTest(
    @Autowired private val developService: DevelopService,
) {

    @DisplayName("서버명 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetServerName_thenReturn() {
        // given
        val serverName = "Alloon Test"

        // when
        val gotServerName = developService.getServerName()

        // then
        assertThat(gotServerName).isEqualTo(serverName)
    }
}