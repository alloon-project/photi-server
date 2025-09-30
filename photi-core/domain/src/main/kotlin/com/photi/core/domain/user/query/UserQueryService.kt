package com.photi.core.domain.user.query

import com.photi.core.domain.user.model.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserQueryService(
    private val userRepository: UserRepository,
) {

    fun getUserBy(email: String) = userRepository.findByEmail(email)

    fun getUserBy(id: Long) = userRepository.findById(id)

    fun getLoginUserBy(username: String) = userRepository.findByUsername(username)

    fun getAuthenticatedUserBy(email: String) =
        userRepository.findByEmailAndAuthenticatedTrue(email)

    fun getAuthenticatedUserBy(email: String, username: String) =
        userRepository.findByEmailAndUsernameAndAuthenticatedTrue(email, username)

    fun existsActiveUserBy(email: String) = userRepository.existsByEmailAndDeletedFalse(email)

    fun existsUserBy(username: String) = userRepository.existsByUsername(username)
}
