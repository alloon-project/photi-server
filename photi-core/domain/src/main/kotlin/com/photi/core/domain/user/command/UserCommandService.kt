package com.photi.core.domain.user.command

import com.photi.core.domain.user.dto.SendEmailAuthenticationCodeDto
import com.photi.core.domain.user.model.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
) {

    fun createUser(dto: SendEmailAuthenticationCodeDto) {
        userRepository.save(dto.toUserEntity())
    }
}
