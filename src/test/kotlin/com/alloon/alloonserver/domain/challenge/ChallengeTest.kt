package com.alloon.alloonserver.domain.challenge

import com.photi.server.service.challenge.dto.ChallengeHashtagDto
import com.photi.server.service.challenge.dto.ChallengeRuleDto
import com.photi.server.service.challenge.dto.CreateChallengeDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class ChallengeTest {

    @DisplayName("챌린지 개별 조회시 방문 수가 증가한다.")
    @Test
    fun givenValid_whenUpdateVisitCnt_thenReturn() {
        // given
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")

        // when
        challenge.updateVisitCnt()

        // then
        assertThat(challenge.visitCnt).isEqualTo(1)
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
}