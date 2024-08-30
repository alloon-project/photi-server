package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeRuleDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class ChallengeMemberTest {

    @DisplayName("개인목표 작성이 정상 작동한다")
    @Test
    fun givenValid_whenUpdateGoal_thenReturn() {
        // given
        val contact =
            Contact(email = "tester@photi.com", verificationCode = "000000", verifyYn = true)
        val user =
            User(contact = contact, username = "tester", password = "password1!", imageUrl = "")
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        val goal = "개인목표"

        // when
        challengeMember.updateGoal(goal)

        // then
        assertThat(challengeMember.goal).isEqualTo(goal)
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