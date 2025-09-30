package com.photi.core.domain.user.port.email

import com.photi.utils.CodeUtil.getAuthenticationCode
import com.photi.utils.PasswordUtil.getTemporaryPassword

sealed class EmailMessage(
    val to: String,
    val value: String,
    val template: EmailTemplate,
) {

    class SignUpAuthenticationCode(to: String) :
        EmailMessage(to, getAuthenticationCode(), EmailTemplate.SIGN_UP_AUTHENTICATION_CODE)

    class FindUsername(to: String, username: String) :
        EmailMessage(to, username, EmailTemplate.FIND_USERNAME)

    class FindPassword(to: String) :
        EmailMessage(to, getTemporaryPassword(), EmailTemplate.FIND_PASSWORD)
}
