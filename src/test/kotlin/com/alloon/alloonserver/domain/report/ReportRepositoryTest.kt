package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType.MISSION
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionHashtagDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionRuleDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
class ReportRepositoryTest(
    @Autowired private val reportRepository: ReportRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val missionRepository: MissionRepository,
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @DisplayName("신고 식별자로 신고 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_whenReturn() {
        // given
        val user = createAndSaveUserWithContact()
        val mission = createAndSaveMission()
        val report = createAndSaveReport(user, MISSION, mission = mission)

        // when
        val result = reportRepository.find(user.id!!)

        // then
        assertThat(result).isEqualTo(report)
    }

    private fun createAndSaveReport(
        reporter: User,
        type: ReportCategoryType,
        missionMember: MissionMember? = null,
        mission: Mission? = null,
        feed: Feed? = null
    ): Report {
        return reportRepository.save(
            Report(
                reportCategory = createAndSaveReportCategory(type),
                reporter = reporter,
                missionMember = missionMember,
                mission = mission,
                feed = feed
            )
        )
    }

    private fun createAndSaveReportCategory(type: ReportCategoryType): ReportCategory {
        return reportCategoryRepository.save(ReportCategory(admin = null, type = type, description = "신고 사유", sort = 1))
    }

    private fun createAndSaveMission(): Mission {
        val dto = getCreateMissionDto()
        val mission = Mission.toEntity(dto)
        return missionRepository.save(mission)
    }

    private fun getCreateMissionDto(): CreateMissionDto {
        return CreateMissionDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf(
                CreateMissionRuleDto("챌린지 인증 룰1"),
                CreateMissionRuleDto("챌린지 인증 룰2"),
                CreateMissionRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                CreateMissionHashtagDto("해시태그 1"),
                CreateMissionHashtagDto("해시태그 2"),
            )
        )
    }

    private fun createAndSaveUserWithContact(): User {
        val contact = contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        return userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = encryptedPassword,
                imageUrl = ""
            )
        )
    }
}