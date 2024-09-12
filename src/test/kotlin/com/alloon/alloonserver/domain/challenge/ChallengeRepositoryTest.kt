package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.ChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.ChallengeRuleDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
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

    @DisplayName("챌린지 개최 날짜 기준 최신순으로 정렬된 챌린지 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindAllOrderByStartDate_thenReturnSlice() {
        // given
        val pageable = PageRequest.of(0, 10)
        val firstChallenge = getChallenge(2L, LocalDate.of(2025, 9, 1))
        val secondChallenge = getChallenge(1L, LocalDate.of(2024, 9, 1))
        val thirdChallenge = getChallenge(5L, LocalDate.of(2024, 7, 30))
        val challenges = listOf(
            secondChallenge,
            firstChallenge,
            getChallenge(3L, LocalDate.of(2024, 7, 1)),
            getChallenge(4L, LocalDate.of(2023, 10, 14)),
            thirdChallenge,
        )

        challengeRepository.saveAll(challenges)

        // when
        val result = challengeRepository.findAllOrderByStartDate(pageable)

        // then
        assertThat(result.content[0].id).isEqualTo(firstChallenge.id)
        assertThat(result.content[1].id).isEqualTo(secondChallenge.id)
        assertThat(result.content[2].id).isEqualTo(thirdChallenge.id)
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

    private fun getChallenge(id: Long, startDate: LocalDate): Challenge {
        return Challenge(
            id = id,
            name = "챌린지 이름",
            isPublic = true,
            goal = "챌린지 목표입니다.",
            proveTime = LocalTime.of(13, 0),
            endDate = LocalDate.of(2024, 12, 1),
            imageUrl = "https://url.kr/5MhHhD",
            startDate = startDate,
        )
    }
}