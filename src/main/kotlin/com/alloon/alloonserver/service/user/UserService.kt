package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.s3.FolderType.USERS
import com.alloon.alloonserver.service.s3.S3Service
import com.alloon.alloonserver.service.user.dto.FindUserChallengeCntDto
import com.alloon.alloonserver.service.user.dto.FindUserFeedsByDateDto
import com.alloon.alloonserver.service.user.dto.UserChallengeHistoryDto
import com.alloon.alloonserver.service.user.dto.UserInfoDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
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

    private fun deleteOriginalImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            s3Service.deleteImage(imageUrl, USERS)
        }
    }
}