package com.alloon.alloonserver.api.service.email

import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_SEND_ERROR
import com.alloon.alloonserver.common.response.CustomException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.thymeleaf.spring6.SpringTemplateEngine

@ActiveProfiles("test")
@SpringBootTest
class EmailServiceTest(
    @Autowired private val emailService: EmailService,
    @Autowired private val mailSender: JavaMailSender,
    @Autowired private val templateEngine: SpringTemplateEngine,
) {

    @DisplayName("인증코드 이메일 전송이 정상 작동한다")
    @Test
    fun givenValid_whenSendVerificationEmail_thenReturn() {
        // given
        val toAddress = "tester@alloon.com"
        val key = "000000"

        // when & then
        emailService.sendVerificationEmail(toAddress, key)
    }

    // TODO
//    @DisplayName("잘못된 이메일로 이메일 전송을 하면 예외가 발생한다")
//    @Test
//    fun givenInvalidEmail_whenSendVerificationEmail_thenThrow() {
//        // given
//        val toAddress = "tester"
//        val key = "000000"
//
//        // when & then
//        assertThatThrownBy{ emailService.sendVerificationEmail(toAddress, key) }
//            .isInstanceOf(CustomException::class.java)
//            .extracting("exceptionCode")
//            .isEqualTo(EMAIL_SEND_ERROR)
//    }
}