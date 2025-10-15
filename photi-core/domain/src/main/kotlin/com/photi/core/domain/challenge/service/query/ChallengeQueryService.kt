package com.photi.core.domain.challenge.service.query

import com.photi.core.domain.challenge.model.repository.ChallengeExampleImagesRepository
import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import com.photi.core.domain.common.model.ServiceStatus
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeQueryService(
    private val challengeRepository: ChallengeRepository,
    private val challengeExampleImagesRepository: ChallengeExampleImagesRepository,
) {

    fun existsBy(id: Long) =
        challengeRepository.existsByIdAndServiceStatus(id, ServiceStatus.ACTIVE)

    fun getExampleImages() = challengeExampleImagesRepository.findExampleImages()

    fun getPopularChallenges() = challengeRepository.findPopularChallenges()

    fun getChallengeIntroBy(id: Long) = challengeRepository.findChallengeIntroById(id)

    fun getChallenges(page: Int, size: Int) =
        challengeRepository.findChallenges(PageRequest.of(page, size))

    fun getBy(id: Long) = challengeRepository.findById(id)

    fun getInvitationCodeBy(id: Long) = challengeRepository.findInvitationCodeById(id)

    fun getChallengeBy(id: Long) = challengeRepository.findChallengeById(id)

    fun getChallengesBy(hashtags: Set<String>, page: Int, size: Int) =
        challengeRepository.findChallengesByHashtags(hashtags, PageRequest.of(page, size))

    fun getChallengesBy(hashtag: String, page: Int, size: Int) =
        challengeRepository.findChallengesBySpecificHashtag(hashtag, PageRequest.of(page, size))

    fun getNameChallengesBy(name: String, page: Int, size: Int) =
        challengeRepository.findChallengesByName(name, PageRequest.of(page, size))

    fun getHashtagChallengesBy(hashtag: String, page: Int, size: Int) =
        challengeRepository.findChallengesByHashtag(hashtag, PageRequest.of(page, size))
}
