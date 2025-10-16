package com.photi.core.domain.user.port.email

sealed class EmailMessage(
    val to: String,
    val value: String,
    val template: EmailTemplate,
) {

    class SignUpAuthenticationCode(to: String, authenticationCode: String) :
        EmailMessage(to, authenticationCode, EmailTemplate.SIGN_UP_AUTHENTICATION_CODE)

    class FindUsername(to: String, username: String) :
        EmailMessage(to, username, EmailTemplate.FIND_USERNAME)

    class FindPassword(to: String, temporaryPassword: String) :
        EmailMessage(to, temporaryPassword, EmailTemplate.FIND_PASSWORD)
}
