package com.alloon.alloonserver.service.report

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.*
import com.alloon.alloonserver.domain.user.Role.ADMIN
import com.alloon.alloonserver.domain.user.Role.USER
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeRuleDto
import com.alloon.alloonserver.service.report.dto.ReportCreateServiceDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class ReportServiceTest(
    @Autowired private val reportService: ReportService,
    @Autowired private val reportRepository: ReportRepository,
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val challengeRepository: ChallengeRepository,
    @Autowired private val challengeMemberRepository: ChallengeMemberRepository,
    @Autowired private val feedRepository: FeedRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @ParameterizedTest(name = "[{index}] {0} 종류의 신고 항목 설명 전체 조회가 정상 작동한다")
    @EnumSource(ReportCategoryType::class)
    @DisplayName("신고 항목 설명 전체 조회가 정상 작동한다")
    fun givenEnum_whenGetAllReportCategoryDescription_thenReturn(type: ReportCategoryType) {
        // given
        val adminRole = createAndSaveUserWithContact("tester@alloon.com", "tester", ADMIN)
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)

        // when
        val result = reportService.getAllReportCategoryDescription(type.name)

        // then
        assertThat(result).containsExactly(reportCategory.description)
    }

    @ParameterizedTest(name = "[{index}] {0} 종류의 신고 등록이 정상 작동한다")
    @EnumSource(ReportCategoryType::class)
    @DisplayName("신고 등록이 정상 작동한다")
    fun givenEnum_whenCreateReport_thenReturn(type: ReportCategoryType) {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val reportTargetId = when (type) {
            CHALLENGE -> createAndSaveChallenge().id
            CHALLENGE_MEMBER -> createAndSaveChallengeMember().id
            FEED -> createAndSaveFeed().id
        }
        val request =
            createValidReportCreateServiceRequest(reportTargetId!!, type, reportCategory.id!!)

        // when
        reportService.createReport(adminRole.user.id!!, request)

        // then
        val report = reportRepository.find(adminRole.user.id!!)

        assertThat(report)
            .extracting("reportCategory", "reporter", "reason")
            .containsExactly(reportCategory, adminRole.user, request.reportReason)
    }

    @DisplayName("존재하지 않은 신고자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingReporter_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = CHALLENGE
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)

        // when & then
        assertThatThrownBy { reportService.createReport(99L, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 신고 항목으로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingReportCategory_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = CHALLENGE
        val request = createValidReportCreateServiceRequest(1L, type, 1)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(REPORT_CATEGORY_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 챌린지 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetChallenge_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = CHALLENGE
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(99L, type, reportCategory.id!!)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 챌린지 멤버 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetChallengeMember_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = CHALLENGE_MEMBER
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(99L, type, reportCategory.id!!)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 피드 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetFeed_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = FEED
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(99L, type, reportCategory.id!!)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    private fun createValidReportCreateServiceRequest(
        reportTargetId: Long,
        type: ReportCategoryType,
        reportCategoryId: Int,
    ): ReportCreateServiceDto {
        return ReportCreateServiceDto(reportTargetId, type.name, reportCategoryId, "신고 사유")
    }

    private fun createAndSaveFeed(): Feed {
        val challengeMember = createAndSaveChallengeMember()
        return feedRepository.save(
            Feed(
                challengeMember = challengeMember,
                challenge = challengeMember.challenge,
                imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png"
            )
        )
    }

    private fun createAndSaveChallengeMember(): ChallengeMember {
        val userRole = createAndSaveUserWithContact("tester2@alloon.com", "tester2", USER)
        val challenge = createAndSaveChallenge()
        return challengeMemberRepository.save(
            ChallengeMember(
                user = userRole.user,
                challenge = challenge,
                isCreator = false
            )
        )
    }

    private fun createAndSaveChallenge(): Challenge {
        val dto = getCreateChallengeDto()
        val challenge = dto.toEntity("https://url.kr/5MhHhD")
        return challengeRepository.save(challenge)
    }

    private fun getCreateChallengeDto(): CreateChallengeDto {
        return CreateChallengeDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                CreateChallengeRuleDto("챌린지 인증 룰1"),
                CreateChallengeRuleDto("챌린지 인증 룰2"),
                CreateChallengeRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                CreateChallengeHashtagDto("해시태그 1"),
                CreateChallengeHashtagDto("해시태그 2"),
            )
        )
    }

    private fun createAndSaveReportCategory(
        admin: User,
        type: ReportCategoryType,
        description: String,
        sort: Int,
    ): ReportCategory {
        return reportCategoryRepository.save(
            ReportCategory(
                admin = admin,
                type = type,
                description = description,
                sort = sort
            )
        )
    }

    private fun createAndSaveUserWithContact(
        email: String,
        username: String,
        role: Role
    ): UserRole {
        val contact = contactRepository.save(
            Contact(
                email = email,
                verificationCode = "000000",
                verifyYn = true
            )
        )

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(
            User(
                contact = contact,
                username = username,
                password = encryptedPassword,
                imageUrl = ""
            )
        )

        return userRoleRepository.save(UserRole(user = user, role = role))
    }
}