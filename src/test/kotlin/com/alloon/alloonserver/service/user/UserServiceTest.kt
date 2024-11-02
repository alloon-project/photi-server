package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.UserImageDto
import com.alloon.alloonserver.service.s3.S3Service
import com.alloon.alloonserver.service.user.dto.*
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val s3Service = mockk<S3Service>()

    private val userService = UserService(userRepository, s3Service)

    @DisplayName("사용자 정보 조회를 하면 일치하는 사용자 정보를 반환한다.")
    @Test
    fun givenValid_whenFindUserInfo_thenReturn() {
        // given
        val userId = 1L
        val dto = getUserInfoDto()

        every { userRepository.findInfoById(any()) } returns dto

        // when
        val result = userService.findUserInfo(userId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 사용자를 찾으려고 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenFindUserInfo_thenThrow() {
        // given
        val userId = 1L

        every { userRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { userService.findUserInfo(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("사용자 프로필 이미지 업로드를 하면 일치하는 사용자 정보를 반환한다.")
    @Test
    fun givenValid_whenUpdateUserImage_thenReturn() {
        // given
        val userId = 1L
        val multipartFile = getMultipartFile()
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val dto = getUserInfoDto()

        every { userRepository.findById(any()) } returns Optional.of(user)
        every { s3Service.deleteImage(any(), any()) } just Runs
        every { s3Service.uploadImage(any(), any()) } returns ""
        every { s3Service.getImageUrl(any()) } returns imageUrl

        // when
        val result = userService.updateUserImage(userId, multipartFile)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 사용자로 이미지 업로드를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenUpdateUserImage_thenThrow() {
        // given
        val userId = 1L
        val multipartFile = getMultipartFile()

        every { userRepository.findById(any()) } returns Optional.empty()

        // when & then
        assertThatThrownBy { userService.updateUserImage(userId, multipartFile) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("사용자 챌린지 기록 조회를 하면 일치하는 사용자 챌린지 기록을 반환한다.")
    @Test
    fun givenValid_whenFindUserChallengeHistory_thenReturn() {
        // given
        val userId = 1L
        val dto = getUserChallengeHistoryDto()

        every { userRepository.findChallengeHistoryById(any()) } returns dto

        // when
        val result = userService.findUserChallengeHistory(userId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 사용자로 챌린지 기록 조회를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenFindUserChallengeHistory_thenThrow() {
        // given
        val userId = 1L

        every { userRepository.findChallengeHistoryById(any()) } returns null

        // when & then
        assertThatThrownBy { userService.findUserChallengeHistory(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("사용자 피드 인증 날짜 리스트 조회를 하면 일치하는 사용자 피드 인증 날짜 리스트를 반환한다.")
    @Test
    fun givenValid_whenFindUserFeeds_thenReturn() {
        // given
        val userId = 1L
        val feeds = listOf("2024-10-02", "2024-10-05", "2024-10-07")

        every { userRepository.findFeedsById(any()) } returns feeds

        // when
        val result = userService.findUserFeeds(userId)

        // then
        assertThat(result).isEqualTo(feeds)
    }

    @DisplayName("존재하지 않은 사용자로 피드 인증 날짜 리스트 조회를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenFindUserFeeds_thenThrow() {
        // given
        val userId = 1L

        every { userRepository.findFeedsById(any()) } returns null

        // when & then
        assertThatThrownBy { userService.findUserFeeds(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("사용자 참여 중인 챌린지 갯수 조회를 하면 일치하는 사용자 챌린지 갯수를 반환한다.")
    @Test
    fun givenValid_whenFindUserChallengeCnt_thenReturn() {
        // given
        val userId = 1L
        val userChallenge = FindUserChallengeCntDto("tester", 5)

        every { userRepository.findChallengeCntById(any()) } returns userChallenge

        // when
        val result = userService.findUserChallengeCnt(userId)

        // then
        assertThat(result).isEqualTo(userChallenge)
    }

    @DisplayName("존재하지 않은 사용자로 참여 중인 챌린지 갯수 조회를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenFindUserChallengeCnt_thenThrow() {
        // given
        val userId = 1L

        every { userRepository.findChallengeCntById(any()) } returns null

        // when & then
        assertThatThrownBy { userService.findUserChallengeCnt(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("사용자 피드 인증 개별 날짜 조회를 하면 일치하는 날짜의 인증 피드 리스트를 반환한다.")
    @Test
    fun givenValid_whenFindUserFeedsByDate_thenReturn() {
        // given
        val userId = 1L
        val date = LocalDate.now()
        val dto = getFindUserFeedsByDateDto()
        val userFeeds = listOf(dto, dto, dto)

        every { userRepository.findFeedsByDate(any(), any()) } returns userFeeds

        // when
        val result = userService.findUserFeedsByDate(userId, date)

        // then
        assertThat(result.size).isEqualTo(3)
    }

    @DisplayName("사용자 피드 인증 횟수 모아보기 조회를 하면 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindUserFeedHistory_thenReturn() {
        // given
        val userId = 1L
        val dto = FindUserFeedHistoryDto(1L, "https://url.kr/5MhHhD", LocalDateTime.now(), "챌린지 이름")
        val content = listOf(dto, dto, dto)
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { userRepository.findFeedHistoryById(any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val result = userService.findUserFeedHistory(userId, pageable)

        // then
        assertThat(result.content.size).isEqualTo(3)
    }

    @DisplayName("사용자 종료된 챌린지 조회를 하면 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindUserEndedChallenges_thenReturn() {
        // given
        val userId = 1L
        val dto = getFindUserEndedChallengesDto()
        val content = listOf(dto, dto, dto)
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { userRepository.findEndedChallengesById(any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val result = userService.findUserEndedChallenges(userId, pageable)

        // then
        assertThat(result.content.size).isEqualTo(3)
    }

    @DisplayName("사용자 참여 중인 챌린지 조회를 하면 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindUserChallenges_thenReturn() {
        // given
        val userId = 1L
        val dto = getFindUserChallengesDto()
        val content = listOf(dto, dto, dto)
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { userRepository.findUserChallengesById(any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val result = userService.findUserChallenges(userId, pageable)

        // then
        assertThat(result.content.size).isEqualTo(3)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }

    private fun getUserInfoDto(): UserInfoDto {
        return UserInfoDto("https://url.kr/5MhHhD", "tester", "tester@photi.com")
    }

    private fun getMultipartFile(): MockMultipartFile {
        return MockMultipartFile("imageFile", "file.png", "image/png", ByteArray(1))
    }

    private fun getUserChallengeHistoryDto(): UserChallengeHistoryDto {
        return UserChallengeHistoryDto("tester", "https://url.kr/5MhHhD", 99, 2)
    }

    private fun getFindUserFeedsByDateDto(): FindUserFeedsByDateDto {
        return FindUserFeedsByDateDto(
            1L,
            "https://url.kr/5MhHhD",
            "챌린지 이름",
            LocalTime.of(13, 0)
        )
    }

    private fun getFindUserEndedChallengesDto(): FindUserEndedChallengesDto {
        return FindUserEndedChallengesDto(
            1L,
            "챌린지 이름",
            "https://url.kr/5MhHhD",
            LocalDate.of(2024, 12, 1),
            3,
            listOf(
                UserImageDto(1L, "https://url.kr/5MhHhD"),
                UserImageDto(1L, "https://url.kr/5MhHhE"),
                UserImageDto(1L, "https://url.kr/5MhHhF")
            )
        )
    }

    private fun getFindUserChallengesDto(): FindUserChallengesDto {
        return FindUserChallengesDto(
            1L,
            "챌린지 이름",
            "https://url.kr/5MhHhD",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf("러닝", "건강"),
            "https://url.kr/5MhHhD",
        )
    }
}