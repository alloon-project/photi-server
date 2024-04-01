package com.alloon.alloonserver.common.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class PasswordUtilityTest(
    @Autowired private val passwordUtility: PasswordUtility,
    @Autowired private val passwordEncoder: PasswordEncoder,
) {

    @DisplayName("랜덤 코드 생성이 정상 작동한다")
    @Test
    fun givenValid_whenGenerateRandomCode_thenReturn() {
        // given
        val length = 6

        // when
        val code = PasswordUtility.generateRandomCode(length)

        // then
        assertThat(code.length).isEqualTo(length)
    }
}