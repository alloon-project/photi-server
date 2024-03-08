package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.email.EmailService
import com.alloon.alloonserver.api.service.user.request.ContactServiceSendVerificationRequest
import com.alloon.alloonserver.api.service.user.request.ContactServiceVerifyRequest
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(readOnly = true)
class AuthService(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val passwordUtility: PasswordUtility,
) {

    /**
     * 이메일 인증코드 전송
     * @param request 연락처 인증코드 전송 요청
     * @throws EXISTING_EMAIL 409
     * @throws EMAIL_SEND_ERROR 500
     */
    @Transactional
    fun sendVerificationCode(@Valid request: ContactServiceSendVerificationRequest) {
        val verificationCode = passwordUtility.generateRandomCode(6)

        contactRepository.findByEmail(request.email)
            ?.let { foundContact ->
                if (userRepository.existsByContact(foundContact))
                    throw CustomException(EXISTING_EMAIL)

                foundContact.changeVerificationCode(verificationCode)
            } ?: run {
            contactRepository.save(request.toEntity(verificationCode))
        }

        emailService.sendVerificationEmail(request.email, verificationCode)
    }

    /**
     * 이메일 인증코드 검증
     * @param request 연락처 인증코드 검증 요청
     * @throws VERIFICATION_CODE_INVALID 400
     * @throws EMAIL_NOT_FOUND 404
     */
    @Transactional
    fun verifyEmailVerificationCode(@Valid request: ContactServiceVerifyRequest) {
        val contact = contactRepository.findByEmail(request.email)
            ?.verify(request.verificationCode)
            ?: throw CustomException(EMAIL_NOT_FOUND)
    }
}