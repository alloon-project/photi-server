package com.photi.server.service.email

import com.photi.server.common.constant.EmailConstants
import com.photi.server.common.constant.ExceptionCode.EMAIL_SEND_ERROR
import com.photi.server.common.response.CustomException
import org.springframework.beans.factory.annotation.Value
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
     * 이메일 전송 |
     * @param toAddress 받는 주소
     * @param key 인증 코드
     * @param constants 이메일 종류
     * @throws EMAIL_SEND_ERROR 500
     */
    @Async
    fun sendEmail(toAddress: String, key: String, constants: EmailConstants) {
        val context = Context().apply {
            setVariables(mapOf(
                "intro" to constants.content,
                "key" to key,
            ))
        }
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, "utf-8")

        with(mimeMessageHelper) {
            setFrom(fromAddress)
            setTo(toAddress)
            setSubject(constants.subject)
            setText(templateEngine.process("mail", context), true)
        }

        try {
            mailSender.send(mimeMessage)
        } catch (e: MailSendException) {
            throw CustomException(EMAIL_SEND_ERROR)
        }
    }
}