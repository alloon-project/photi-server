package com.photi.core.domain.user.service.command

import com.photi.core.domain.user.dto.SendEmailAuthenticationCodeDto
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.User
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

    fun createUser(oAuthInfo: OAuthInfo, email: String, image: String) =
        userRepository.save(toUserEntity(oAuthInfo, email, image))

    private fun toUserEntity(oAuthInfo: OAuthInfo, email: String, image: String) =
        User(
            email = email,
            oAuthInfo = oAuthInfo,
            role = RoleType.USER,
            isAuthenticated = true,
            imageUrl = image,
        )
}
