package com.photi.core.domain.user.service

import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.user.dto.FindChallengeHistoryDto
import com.photi.core.domain.user.dto.FindImagePreSignedUrlDto
import com.photi.core.domain.user.dto.UpdateProfileImageDto
import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.port.UserS3Port
import com.photi.core.domain.user.service.query.UserQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class UserService(
    private val userQueryService: UserQueryService,
    private val s3Port: UserS3Port,
) {

    fun findInfo(userId: Long) = userQueryService.getInfoBy(userId)
        ?: throw UserException.NotFoundUserException()

    fun findImagePreSignedUrl(dto: FindImagePreSignedUrlDto) =
        s3Port.getPreSignedUrl(dto.imageName, DirectoryType.USERS)

    @Transactional
    fun updateProfileImage(userId: Long, dto: UpdateProfileImageDto) {
        val user = userQueryService.getUserBy(userId).orElseThrow {
            throw UserException.NotFoundUserException()
        }
        user.changeImageUrl(s3Port, dto.imageUrl)
    }

    fun findChallengeHistory(userId: Long): FindChallengeHistoryDto {
        val user = userQueryService.getUserBy(userId).orElseThrow {
            throw UserException.NotFoundUserException()
        }
        return userQueryService.getChallengeHistoryBy(userId) ?: FindChallengeHistoryDto.of(user)
    }

    fun findFeedDates(userId: Long) = userQueryService.getFeedDatesBy(userId)

    fun findChallengeCount(userId: Long) = userQueryService.getChallengeCountBy(userId)
        ?: throw UserException.NotFoundUserException()

    fun findFeedsByDate(userId: Long, date: LocalDate) = userQueryService.getFeedsBy(userId, date)

    fun findFeedHistory(userId: Long, page: Int, size: Int) =
        userQueryService.getFeedHistoryBy(userId, page, size)

    fun findEndedChallenges(userId: Long, page: Int, size: Int) =
        userQueryService.getEndedChallengesBy(userId, page, size)

    fun findChallenges(userId: Long, page: Int, size: Int) =
        userQueryService.getChallengesBy(userId, page, size)

    fun findChallengeIsProve(userId: Long, challengeId: Long) =
        userQueryService.getChallengeIsProveBy(userId, challengeId)
}
