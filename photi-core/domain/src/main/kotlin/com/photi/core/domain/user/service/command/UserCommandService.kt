package com.photi.core.domain.user.service.command

import com.photi.core.domain.user.dto.OAuthSignUpDto
import com.photi.core.domain.user.dto.SendEmailAuthenticationCodeDto
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.model.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
) {

    fun createUser(dto: SendEmailAuthenticationCodeDto, authenticationCode: String) {
        userRepository.save(dto.toEntity(authenticationCode))
    }

    fun createUser(dto: OAuthSignUpDto, oAuthInfo: OAuthInfo, email: String) =
        userRepository.save(dto.toEntity(oAuthInfo, email))
}
