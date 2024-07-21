package com.alloon.alloonserver.service.email

import com.alloon.alloonserver.common.constant.EmailConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.thymeleaf.spring6.SpringTemplateEngine

@ActiveProfiles("test")
class EmailServiceTest(
    private val emailService: EmailService = Mockito.mock(EmailService::class.java)
) {


//    @DisplayName("이메일 전송이 정상 작동한다")
//    @ParameterizedTest(name = "[{index}] {0} 를 이메일 전송시 정상 작동한다")
//    @EnumSource(value = EmailConstants::class)
//    fun givenValid_whenSendEmail_thenReturn(constants: EmailConstants) {
//        // given
//        val toAddress = "tester@alloon.com"
//        val key = "000000"
//
//        // when & then
//        emailService.sendEmail(toAddress, key, constants)
//    }

    // TODO
//    @DisplayName("잘못된 이메일로 이메일 전송을 하면 예외가 발생한다")
//    @Test
//    fun givenInvalidEmail_whenSendEmail_thenThrow() {
//        // given
//        val toAddress = "tester"
//        val key = "000000"
//
//        // when & then
//        assertThatThrownBy{ emailService.sendEmail(toAddress, key) }
//            .isInstanceOf(CustomException::class.java)
//            .extracting("exceptionCode")
//            .isEqualTo(EMAIL_SEND_ERROR)
//    }
}