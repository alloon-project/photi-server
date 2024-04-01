package com.alloon.alloonserver.api.service.email

import com.alloon.alloonserver.common.constant.EmailConstants.REGISTER_VERIFICATION_CODE
import com.alloon.alloonserver.common.constant.ExceptionCode.EMAIL_SEND_ERROR
import com.alloon.alloonserver.common.response.CustomException
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailService (
    @Value("\${api.mail.address}")
    private val fromAddress: String,

    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) {

    /**
     * 인증코드 이메일 전송 |
     * @param toAddress 받는 주소
     * @param key 인증 코드
     * @throws EMAIL_SEND_ERROR 500
     */
    @Async
    fun sendVerificationEmail(toAddress: String, key: String) {
        val context = Context().apply {
            setVariables(mapOf(
                "intro" to REGISTER_VERIFICATION_CODE.content,
                "key" to key,
            ))
        }
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, "utf-8")

        with(mimeMessageHelper) {
            setFrom(fromAddress)
            setTo(toAddress)
            setSubject(REGISTER_VERIFICATION_CODE.subject)
            setText(templateEngine.process("verification-mail", context), true)
        }

        try {
            mailSender.send(mimeMessage)
        } catch (e: MailSendException) {
            throw CustomException(EMAIL_SEND_ERROR)
        }
    }
}