package com.photi.core.domain.challenge.service

import com.photi.core.domain.challenge.dto.*
import com.photi.core.domain.challenge.exception.ChallengeException
import com.photi.core.domain.challenge.model.HashtagType
import com.photi.core.domain.challenge.port.*
import com.photi.core.domain.challenge.service.command.ChallengeCommandService
import com.photi.core.domain.challenge.service.query.ChallengeQueryService
import com.photi.core.domain.challenge.validator.ChallengeValidator
import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.feed.dto.FindImagePreSignedUrlDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChallengeService(
    private val challengeQueryService: ChallengeQueryService,
    private val challengeCommandService: ChallengeCommandService,
    private val challengeMemberPort: ChallengeChallengeMemberPort,
    private val userChallengeHistoryPort: ChallengeUserChallengeHistoryPort,
    private val challengeHistoryPort: ChallengeChallengeHistoryPort,
    private val feedPort: ChallengeFeedPort,
    private val challengeValidator: ChallengeValidator,
    private val s3Port: ChallengeS3Port,
    private val hashtagService: HashtagService,
) {

    fun findImagePreSignedUrl(dto: FindImagePreSignedUrlDto) =
        s3Port.getPreSignedUrl(dto.imageName, DirectoryType.CHALLENGES)

    @Transactional
    fun createChallenge(userId: Long, dto: CreateChallengeRequestDto): CreateChallengeDto {
        userChallengeHistoryPort.increaseChallenge(userId)
        hashtagService.addHashtags(dto.hashtags)
        val challenge = challengeCommandService.createChallenge(userId, dto)
        challenge.createCreator(userId, challengeMemberPort, challengeHistoryPort)
        return CreateChallengeDto.of(challenge)
    }

    fun findChallengeExampleImages() = challengeQueryService.getExampleImages()

    fun findPopularChallenges() = challengeQueryService.getPopularChallenges()

    fun findChallengeIntro(challengeId: Long) =
        challengeQueryService.getChallengeIntroBy(challengeId)
            ?: throw ChallengeException.NotFoundChallengeException()

    fun findChallenges(page: Int, size: Int) = challengeQueryService.getChallenges(page, size)

    @Transactional
    fun findChallenge(challengeId: Long): FindChallengeDto {
        // todo 동시성 제어 aop
        val challenge = challengeQueryService.getChallengeBy(challengeId)
            ?: throw ChallengeException.NotFoundChallengeException()
        challengeHistoryPort.increaseVisit(challengeId)
        return challenge
    }

    @Transactional
    fun updateChallenge(userId: Long, challengeId: Long, dto: UpdateChallengeDto) {
        val challenge = getChallengeBy(challengeId)
        challengeMemberPort.validateIsCreator(userId, challengeId)
        challenge.change(dto, s3Port, hashtagService)
    }

    @Transactional
    fun withdrawChallenge(userId: Long, challengeId: Long) {
        userChallengeHistoryPort.decreaseChallenge(userId)
        if (leaveChallenge(userId, challengeId)) return
        getChallengeBy(challengeId).delete(s3Port, hashtagService)
    }

    fun findChallengeInvitationCode(
        userId: Long,
        challengeId: Long,
    ): FindChallengeInvitationCodeDto {
        challengeMemberPort.getChallengeMemberIdBy(userId, challengeId)
        return challengeQueryService.getInvitationCodeBy(challengeId)
            ?: throw ChallengeException.NotFoundChallengeException()
    }

    fun findPopularChallengeHashtags() = hashtagService.findPopularChallengeHashtags()

    fun findChallengesByHashtag(
        hashtag: String,
        page: Int,
        size: Int,
    ): SliceDto<FindChallengesDto> {
        return if (hashtag == HashtagType.ALL.value) {
            val hashtags = hashtagService.findPopularChallengeHashtags()
            challengeQueryService.getChallengesBy(hashtags, page, size)
        } else {
            challengeQueryService.getChallengesBy(hashtag, page, size)
        }
    }

    fun searchChallengesByName(name: String, page: Int, size: Int) =
        challengeQueryService.getNameChallengesBy(name, page, size)

    fun searchChallengesByHashtag(hashtag: String, page: Int, size: Int) =
        challengeQueryService.getHashtagChallengesBy(hashtag, page, size)

    @Transactional
    fun joinChallenge(userId: Long, challengeId: Long, dto: RegisterChallengePersonalGoalDto) {
        // todo 동시성 제어 aop
        val challenge = getChallengeBy(challengeId)
        userChallengeHistoryPort.increaseChallenge(userId)
        challenge.createMember(userId, dto, challengeMemberPort, challengeHistoryPort)
    }

    fun findChallengeInvitationCodeMatches(challengeId: Long, invitationCode: String) =
        getChallengeBy(challengeId).validateInvitationCode(challengeValidator, invitationCode)

    fun findChallengeHasFeed(challengeId: Long) = feedPort.existsFeedInChallengeBy(challengeId)

    private fun getChallengeBy(challengeId: Long) =
        challengeQueryService.getBy(challengeId).orElseThrow {
            throw ChallengeException.NotFoundChallengeException()
        }

    private fun leaveChallenge(userId: Long, challengeId: Long): Boolean {
        if (!challengeHistoryPort.validateLastChallengeMember(challengeId)) {
            challengeMemberPort.withdrawMember(userId, challengeId)
            challengeHistoryPort.decreaseChallengeMember(challengeId)
            return true
        }
        return false
    }
}
