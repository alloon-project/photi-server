package com.alloon.alloonserver.api.service.report

import com.alloon.alloonserver.api.service.report.request.ReportCreateServiceRequest
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionMemberRepository
import com.alloon.alloonserver.domain.mission.MissionRepository
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.*
import com.alloon.alloonserver.domain.user.Role.ADMIN
import com.alloon.alloonserver.domain.user.Role.USER
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ReportServiceTest(
    @Autowired private val reportService: ReportService,
    @Autowired private val reportRepository: ReportRepository,
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val missionRepository: MissionRepository,
    @Autowired private val missionMemberRepository: MissionMemberRepository,
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
        val result = reportService.getAllReportCategoryDescription(type)

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
        val reportTargetId = when(type) {
            MISSION -> createAndSaveMission().id
            MISSION_MEMBER -> createAndSaveMissionMember().id
            FEED -> createAndSaveFeed().id
        }
        val request = createValidReportCreateServiceRequest(reportTargetId!!, type, reportCategory.id!!)

        // when
        reportService.createReport(adminRole.user.id!!, request)

        // then
        val report = reportRepository.find(adminRole.user.id!!)

        assertThat(report)
            .extracting("reportCategory", "reporter", "reason")
            .containsExactly(reportCategory, adminRole.user, request.reportReason)
    }

    @DisplayName("올바르지 않은 신고 타입으로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenPatternReportType_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = MISSION
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)
        request.reportType = "WRONG_TYPE"

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .hasMessageContaining(REPORT_TYPE_INVALID.message)
    }

    @DisplayName("120자 초과인 신고 사유로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenSizeGreaterThan120ReportReason_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = MISSION
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)
        request.reportReason = "a".repeat(121)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .hasMessageContaining(REPORT_REASON_LENGTH_INVALID.message)
    }

    @DisplayName("존재하지 않은 신고자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingReporter_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = MISSION
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

        val type = MISSION
        val request = createValidReportCreateServiceRequest(1L, type, 1)

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(REPORT_CATEGORY_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 미션 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetMission_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = MISSION
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)
        request.reportTargetId = 99L

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(MISSION_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 미션 멤버 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetMissionMember_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = MISSION_MEMBER
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)
        request.reportTargetId = 99L

        // when & then
        assertThatThrownBy { reportService.createReport(adminRole.user.id!!, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(MISSION_MEMBER_NOT_FOUND)
    }

    @DisplayName("존재하지 않은 피드 신고 대상자로 신고 등록을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingTargetFeed_whenCreateReport_thenThrows() {
        // given
        val adminRole = createAndSaveUserWithContact("tester1@alloon.com", "tester1", ADMIN)

        val type = FEED
        val reportCategory = createAndSaveReportCategory(adminRole.user, type, "항목", 1)
        val request = createValidReportCreateServiceRequest(1L, type, reportCategory.id!!)
        request.reportTargetId = 99L

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
    ): ReportCreateServiceRequest {
        return ReportCreateServiceRequest(reportTargetId, type.name, reportCategoryId, "신고 사유")
    }

    private fun createAndSaveFeed(): Feed {
        val missionMember = createAndSaveMissionMember()
        return feedRepository.save(Feed(
            missionMember = missionMember,
            mission = missionMember.mission,
            imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png"
        ))
    }

    private fun createAndSaveMissionMember(): MissionMember {
        val userRole = createAndSaveUserWithContact("tester2@alloon.com", "tester2", USER)
        val mission = createAndSaveMission()
        return missionMemberRepository.save(MissionMember(user = userRole.user, mission = mission, creatorYn = false))
    }

    private fun createAndSaveMission(): Mission {
        return missionRepository.save(Mission(
            missionName = "미션명",
            description = "미션 설명",
            imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            endDate = LocalDate.of(2999, 1, 1)
        ))
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

    private fun createAndSaveUserWithContact(email: String, username:String, role: Role): UserRole {
        val contact = contactRepository.save(
            Contact(
                email = email,
                verificationCode = "000000",
                verifyYn = true
            )
        )

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(User(
            contact = contact,
            username = username,
            password = encryptedPassword,
            imageUrl = ""
        ))

        return userRoleRepository.save(UserRole(user = user, role = role))
    }
}