package com.photi.core.domain.user.usecase

import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.challenge.model.repository.ChallengeMemberRepository
import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.s3.model.FolderType
import com.photi.core.domain.s3.usecase.S3Service
import com.photi.core.domain.user.dto.*
import com.photi.core.domain.user.model.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val challengeRepository: ChallengeRepository,
    private val challengeMemberRepository: ChallengeMemberRepository,
    private val s3Service: S3Service,
) {

    fun findUserInfo(userId: Long): UserInfoDto {
        return userRepository.findInfoById(userId)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }

    @Transactional
    fun updateUserImage(userId: Long, imageFile: MultipartFile): UserInfoDto {
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(ExceptionCode.USER_NOT_FOUND)
        }

        deleteOriginalImage(user.imageUrl)
        val fileName = s3Service.uploadImage(imageFile, FolderType.USERS)
        val imageUrl = s3Service.getImageUrl(fileName)

        user.changeImageUrl(imageUrl)

        return UserInfoDto.of(user)
    }

    fun findUserChallengeHistory(userId: Long): UserChallengeHistoryDto {
        return userRepository.findChallengeHistoryById(userId)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }

    fun findUserFeeds(userId: Long): List<String> {
        return userRepository.findFeedsById(userId)?.distinct()
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }

    fun findUserChallengeCnt(userId: Long): FindUserChallengeCntDto {
        return userRepository.findChallengeCntById(userId)
            ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }

    fun findUserFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto> {
        return userRepository.findFeedsByDate(userId, date)
    }

    fun findUserFeedHistory(
        userId: Long,
        page: Int,
        size: Int,
    ): SliceDto<FindUserFeedHistoryDto> {
        val pageable = PageRequest.of(page, size)
        return userRepository.findFeedHistoryById(userId, pageable)
    }

    fun findUserEndedChallenges(
        userId: Long,
        page: Int,
        size: Int,
    ): SliceDto<FindUserEndedChallengesDto> {
        val pageable = PageRequest.of(page, size)
        return userRepository.findEndedChallengesById(userId, pageable)
    }

    fun findUserChallenges(
        userId: Long,
        page: Int,
        size: Int,
    ): SliceDto<FindUserChallengesDto> {
        val pageable = PageRequest.of(page, size)
        return userRepository.findUserChallengesById(userId, pageable)
    }

    fun findUserChallengeIsProve(userId: Long, challengeId: Long): Boolean {
        validateChallenge(challengeId)
        validateChallengeMember(userId, challengeId)
        return userRepository.findIsProveByChallengeId(userId, challengeId)
    }

    private fun deleteOriginalImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            s3Service.deleteImage(imageUrl, FolderType.USERS)
        }
    }

    private fun validateChallenge(challengeId: Long): Challenge {
        return challengeRepository.findInfoById(challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)
    }

    private fun validateChallengeMember(userId: Long, challengeId: Long): ChallengeMember {
        return challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND)
    }
}
