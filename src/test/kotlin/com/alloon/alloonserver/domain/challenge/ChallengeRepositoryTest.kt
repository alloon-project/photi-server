package com.alloon.alloonserver.domain.challenge

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
class ChallengeRepositoryTest(
    @Autowired private val challengeRepository: ChallengeRepository,
) {

    @DisplayName("챌린지 식별자로 챌린지 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val challenge = createAndSaveChallenge()

        // when
        val result = challengeRepository.find(challenge.id!!)

        // then
        assertThat(result).isEqualTo(challenge)
    }

    @DisplayName("챌린지 id로 조회하면 일치하는 챌린지를 반환한다")
    @Test
    fun givenValid_whenFindInfoById_thenReturnChallenge() {
        // given
        val challenge = createAndSaveChallenge()

        // when
        val result = challenge.id?.let { challengeRepository.findInfoById(it) }

        // then
        assertThat(result).isEqualTo(challenge)
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
}