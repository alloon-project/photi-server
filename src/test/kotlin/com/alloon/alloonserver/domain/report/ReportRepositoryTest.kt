package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType.CHALLENGE
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeRuleDto
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
    @Autowired private val challengeRepository: ChallengeRepository,
    @Autowired private val reportCategoryRepository: ReportCategoryRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @DisplayName("신고 식별자로 신고 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_whenReturn() {
        // given
        val user = createAndSaveUserWithContact()
        val challenge = createAndSaveChallenge()
        val report = createAndSaveReport(user, CHALLENGE, challenge = challenge)

        // when
        val result = reportRepository.find(user.id!!)

        // then
        assertThat(result).isEqualTo(report)
    }

    private fun createAndSaveReport(
        reporter: User,
        type: ReportCategoryType,
        challengeMember: ChallengeMember? = null,
        challenge: Challenge? = null,
        feed: Feed? = null
    ): Report {
        return reportRepository.save(
            Report(
                reportCategory = createAndSaveReportCategory(type),
                reporter = reporter,
                challengeMember = challengeMember,
                challenge = challenge,
                feed = feed
            )
        )
    }

    private fun createAndSaveReportCategory(type: ReportCategoryType): ReportCategory {
        return reportCategoryRepository.save(ReportCategory(admin = null, type = type, description = "신고 사유", sort = 1))
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