package com.alloon.alloonserver.service.report

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportReasonType
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.ChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.ChallengeRuleDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import com.alloon.alloonserver.service.report.dto.CreateReportDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class ReportServiceTest {

    private val reportRepository = mockk<ReportRepository>()
    private val challengeRepository = mockk<ChallengeRepository>()
    private val challengeMemberRepository = mockk<ChallengeMemberRepository>()
    private val feedRepository = mockk<FeedRepository>()
    private val userRepository = mockk<UserRepository>()

    private val reportService = ReportService(
        reportRepository,
        challengeRepository,
        challengeMemberRepository,
        feedRepository,
        userRepository
    )

    @DisplayName("신고 등록을 하면 신고 카테고리, 이유, 내용이 저장된다.")
    @Test
    fun givenValid_whenCreateReport_thenReturn() {
        // given
        val userId = 1L
        val targetId = 1L
        val dto = CreateReportDto("CHALLENGE", "DANGEROUS", "신고 내용")
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val report = dto.toReportEntity(userId, targetId)

        every { userRepository.find(any()) } returns user
        every { challengeRepository.find(any()) } returns challenge
        every { reportRepository.save(any()) } returns report

        // when
        reportService.createReport(userId, targetId, dto)

        // then
        assertThat(report.category).isEqualTo(ReportCategoryType.valueOf(dto.category))
        assertThat(report.reason).isEqualTo(ReportReasonType.valueOf(dto.reason))
        assertThat(report.content).isEqualTo(dto.content)
    }

    @DisplayName("존재하지 않은 회원으로 신고 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenCreateReport_thenThrow() {
        // given
        val userId = 1L
        val targetId = 1L
        val dto = CreateReportDto("CHALLENGE", "DANGEROUS", "신고 내용")

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { reportService.createReport(userId, targetId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 챌린지 신고 대상자로 신고 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenCreateReport_thenThrow() {
        // given
        val user = getUser()
        val userId = 1L
        val targetId = 1L
        val dto = CreateReportDto("CHALLENGE", "DANGEROUS", "신고 내용")

        every { userRepository.find(any()) } returns user
        every { challengeRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { reportService.createReport(userId, targetId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 챌린지 멤버 신고 대상자로 신고 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenCreateReport_thenThrow() {
        // given
        val user = getUser()
        val userId = 1L
        val targetId = 1L
        val dto = CreateReportDto("CHALLENGE_MEMBER", "DANGEROUS", "신고 내용")

        every { userRepository.find(any()) } returns user
        every { challengeMemberRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { reportService.createReport(userId, targetId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 피드 신고 대상자로 신고 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeed_whenCreateReport_thenThrow() {
        // given
        val user = getUser()
        val userId = 1L
        val targetId = 1L
        val dto = CreateReportDto("FEED", "DANGEROUS", "신고 내용")

        every { userRepository.find(any()) } returns user
        every { feedRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { reportService.createReport(userId, targetId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    private fun getCreateChallengeDto(): CreateChallengeDto {
        return CreateChallengeDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                ChallengeHashtagDto("해시태그 1"),
                ChallengeHashtagDto("해시태그 2"),
            )
        )
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }
}