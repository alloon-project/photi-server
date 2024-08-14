package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionHashtagDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionRuleDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class MissionMemberTest {

    @DisplayName("개인목표 작성이 정상 작동한다")
    @Test
    fun givenValid_whenUpdateGoal_thenReturn() {
        // given
        val contact = Contact(email = "tester@photi.com", verificationCode = "000000", verifyYn = true)
        val user = User(contact = contact, username = "tester", password = "password1!", imageUrl = "")
        val mission = Mission.toEntity(getCreateMissionDto())
        val missionMember = MissionMember(user = user, mission = mission)

        val goal = "개인목표"

        // when
        missionMember.updateGoal(goal)

        // then
        assertThat(missionMember.goal).isEqualTo(goal)
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
}