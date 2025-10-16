package com.photi.core.domain.user.service.query

import com.photi.core.domain.user.model.RoleType
import com.photi.core.domain.user.model.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class UserQueryService(
    private val userRepository: UserRepository,
) {

    fun getUserBy(email: String) = userRepository.findByEmail(email)

    fun getUserBy(id: Long) = userRepository.findById(id)

    fun getUnAuthenticatedUserBy(email: String) =
        userRepository.findByEmailAndRole(email, RoleType.UNAUTHENTICATED_USER)

    fun getLoginUserBy(username: String) = userRepository.findByUsername(username)

    fun getAuthenticatedUserBy(email: String) =
        userRepository.findByEmailAndIsAuthenticatedTrue(email)

    fun getAuthenticatedUserBy(email: String, username: String) =
        userRepository.findByEmailAndUsernameAndIsAuthenticatedTrue(email, username)

    fun getInfoBy(id: Long) = userRepository.findInfoById(id)

    fun getChallengeHistoryBy(id: Long) = userRepository.findChallengeHistoryById(id)

    fun getFeedDatesBy(id: Long) = userRepository.findFeedDatesById(id)

    fun getChallengeCountBy(id: Long) = userRepository.findChallengeCountById(id)

    fun getFeedsBy(id: Long, date: LocalDate) = userRepository.findFeedsByDate(id, date)

    fun getFeedHistoryBy(id: Long, page: Int, size: Int) =
        userRepository.findFeedHistoryById(id, PageRequest.of(page, size))

    fun getEndedChallengesBy(id: Long, page: Int, size: Int) =
        userRepository.findEndedChallengesById(id, PageRequest.of(page, size))

    fun getChallengesBy(id: Long, page: Int, size: Int) =
        userRepository.findChallengesById(id, PageRequest.of(page, size))

    fun getChallengeIsProveBy(userId: Long, challengeId: Long) =
        userRepository.findChallengeIsProveById(userId, challengeId)

    fun existsEmail(email: String) = userRepository.existsByEmailAndRole(email, RoleType.USER)

    fun existsUsername(username: String) = userRepository.existsByUsername(username)
}
