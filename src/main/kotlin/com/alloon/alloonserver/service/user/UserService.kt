package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.api.controller.user.response.FindUserChallengesResponse
import com.alloon.alloonserver.api.controller.user.response.FindUserEndedChallengesResponse
import com.alloon.alloonserver.api.controller.user.response.FindUserFeedHistoryResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.s3.FolderType.USERS
import com.alloon.alloonserver.service.s3.S3Service
import com.alloon.alloonserver.service.user.dto.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
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
        return userRepository.findInfoById(userId) ?: throw CustomException(USER_NOT_FOUND)
    }

    @Transactional
    fun updateUserImage(userId: Long, imageFile: MultipartFile): UserInfoDto {
        val user = userRepository.findById(userId).orElseThrow {
            throw CustomException(USER_NOT_FOUND)
        }

        deleteOriginalImage(user.imageUrl)
        val fileName = s3Service.uploadImage(imageFile, USERS)
        val imageUrl = s3Service.getImageUrl(fileName)

        user.changeImageUrl(imageUrl)

        return UserInfoDto.of(user)
    }

    fun findUserChallengeHistory(userId: Long): UserChallengeHistoryDto {
        return userRepository.findChallengeHistoryById(userId)
            ?: throw CustomException(USER_NOT_FOUND)
    }

    fun findUserFeeds(userId: Long): List<String> {
        return userRepository.findFeedsById(userId)?.distinct()
            ?: throw CustomException(USER_NOT_FOUND)
    }

    fun findUserChallengeCnt(userId: Long): FindUserChallengeCntDto {
        return userRepository.findChallengeCntById(userId) ?: throw CustomException(USER_NOT_FOUND)
    }

    fun findUserFeedsByDate(userId: Long, date: LocalDate): List<FindUserFeedsByDateDto> {
        return userRepository.findFeedsByDate(userId, date)
    }

    fun findUserFeedHistory(userId: Long, pageable: Pageable): Slice<FindUserFeedHistoryResponse> {
        return userRepository.findFeedHistoryById(userId, pageable)
            .map { FindUserFeedHistoryResponse.of(it) }
    }

    fun findUserEndedChallenges(
        userId: Long,
        pageable: Pageable
    ): Slice<FindUserEndedChallengesResponse> {
        return userRepository.findEndedChallengesById(userId, pageable)
            .map { FindUserEndedChallengesResponse.of(it) }
    }

    fun findUserChallenges(userId: Long, pageable: Pageable): Slice<FindUserChallengesResponse> {
        return userRepository.findUserChallengesById(userId, pageable)
            .map { FindUserChallengesResponse.of(it) }
    }

    fun findUserChallengeIsProve(userId: Long, challengeId: Long): Boolean {
        validateChallenge(challengeId)
        validateChallengeMember(userId, challengeId)
        return userRepository.findIsProveByChallengeId(userId, challengeId)
    }

    private fun deleteOriginalImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            s3Service.deleteImage(imageUrl, USERS)
        }
    }

    private fun validateChallenge(challengeId: Long): Challenge {
        return challengeRepository.findInfoById(challengeId)
            ?: throw CustomException(CHALLENGE_NOT_FOUND)
    }

    private fun validateChallengeMember(userId: Long, challengeId: Long): ChallengeMember {
        return challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
            ?: throw CustomException(CHALLENGE_MEMBER_NOT_FOUND)
    }
}