package com.photi.apis.enduser.config.email

import com.photi.core.domain.user.port.email.EmailTemplate
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.user.port.email.EmailMessage
import com.photi.core.domain.user.port.email.EmailPort
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class EmailAdapter(
    @Value("\${spring.mail.username}")
    private val from: String,
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
) : EmailPort {

    override fun send(message: EmailMessage) {
        val mimeMessage = mailSender.createMimeMessage()
        setMimeMessageHelper(mimeMessage, message.to, message.value, message.template)

        try {
            mailSender.send(mimeMessage)
        } catch (e: MailSendException) {
            throw CustomException(ExceptionCode.EMAIL_SEND_ERROR)
        }
    }

    private fun setMimeMessageHelper(
        mimeMessage: MimeMessage,
        to: String,
        value: String,
        template: EmailTemplate,
    ) {
        with(MimeMessageHelper(mimeMessage, ENCODING)) {
            setFrom(InternetAddress(from, FROM_NAME))
            setTo(to)
            setSubject(template.subject)
            setText(
                templateEngine.process(TEMPLATE_NAME, getContext(template.content, value)),
                true,
            )
        }
    }

    private fun getContext(content: String, value: String) = Context().apply {
        setVariables(mapOf(CONTENT to content, VALUE to value))
    }

    companion object {
        private const val ENCODING = "utf-8"
        private const val FROM_NAME = "포티"
        private const val TEMPLATE_NAME = "mail"
        private const val CONTENT = "content"
        private const val VALUE = "value"
    }
}
