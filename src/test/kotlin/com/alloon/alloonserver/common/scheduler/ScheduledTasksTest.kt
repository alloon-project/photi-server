package com.alloon.alloonserver.common.scheduler

import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.ChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.ChallengeRuleDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import org.assertj.core.api.Assertions.assertThat
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
class ScheduledTasksTest(
    @Autowired private val challengeRepository: ChallengeRepository
) {

    @Test
    fun givenValid_whenBulkServiceStatusEnd_thenScheduleRun() {
        // given
        createAndSaveChallenge(LocalDate.of(2024, 9, 20))
        createAndSaveChallenge(LocalDate.of(2099, 1, 1))
        createAndSaveChallenge(LocalDate.of(2022, 10, 1))
        createAndSaveChallenge(LocalDate.now())

        val result = challengeRepository.bulkServiceStatusEnd()

        // when & then
        assertThat(result).isEqualTo(2)
    }

    private fun createAndSaveChallenge(endDate: LocalDate): Challenge {
        val dto = getCreateChallengeDto(endDate)
        val challenge = dto.toEntity("https://url.kr/5MhHhD", "ABC12")
        return challengeRepository.save(challenge)
    }

    private fun getCreateChallengeDto(endDate: LocalDate): CreateChallengeDto {
        return CreateChallengeDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            endDate,
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
}