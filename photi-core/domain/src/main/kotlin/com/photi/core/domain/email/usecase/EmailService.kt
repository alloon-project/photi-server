package com.photi.core.domain.email.usecase

import com.photi.core.domain.common.consts.EmailConstants
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import jakarta.mail.internet.InternetAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailService(

    @Value("\${spring.mail.username}")
    private val fromAddress: String,
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
) {

    @Async
    fun sendEmail(toAddress: String, key: String, constants: EmailConstants) {
        val context = Context().apply {
            setVariables(
                mapOf(
                    "intro" to constants.content,
                    "key" to key,
                )
            )
        }
        val mimeMessage = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, "utf-8")

        with(mimeMessageHelper) {
            setFrom(InternetAddress(fromAddress, "포티"))
            setTo(toAddress)
            setSubject(constants.subject)
            setText(templateEngine.process("mail", context), true)
        }

        try {
            mailSender.send(mimeMessage)
        } catch (e: MailSendException) {
            throw CustomException(ExceptionCode.EMAIL_SEND_ERROR)
        }
    }
}
