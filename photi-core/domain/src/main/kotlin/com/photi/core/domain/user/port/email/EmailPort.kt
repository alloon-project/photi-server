package com.photi.core.domain.user.port.email

interface EmailPort {

    fun send(message: EmailMessage)
}
