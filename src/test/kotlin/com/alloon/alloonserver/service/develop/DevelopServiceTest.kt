package com.alloon.alloonserver.service.develop

import com.alloon.alloonserver.framework.TestContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * Version 업데이트 여부를 DB 조회를 통해서 진행하고 있는데,
 * Filter 또는 Interceptor 를 통해서 API 호출 시, 내부 코드 또는 파일을 통해서 지정된 값을 읽어와 뱉어내는 것이 좋아보입니다.
 */
//@ActiveProfiles("test")
//@SpringBootTest
//@Transactional
//@ContextConfiguration(initializers = [TestContainerInitializer::class])
//class DevelopServiceTest(
//    @Autowired private val developService: DevelopService,
//) {
//
//    @DisplayName("서버명 조회가 정상 작동한다")
//    @Test
//    fun givenValid_whenGetServerName_thenReturn() {
//        // given
//        val serverName = "Alloon Test"
//
//        // when
//        val gotServerName = developService.getServerName()
//
//        // then
//        assertThat(gotServerName).isEqualTo(serverName)
//    }
//
//    @DisplayName("강제 업데이트 필요 여부 조회를 하면 정상 작동한다")
//    @Test
//    fun givenValid_whenIsNeedForceUpdate_thenReturn() {
//        // given
//        val version = "1.0.0"
//
//        // then
//        val result = developService.needForceUpdate(version)
//
//        // then
//        assertThat(result.updateYn).isNotNull()
//    }
//}