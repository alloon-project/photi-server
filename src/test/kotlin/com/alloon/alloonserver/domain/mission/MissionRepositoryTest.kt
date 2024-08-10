package com.alloon.alloonserver.domain.mission

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
class MissionRepositoryTest(
    @Autowired private val missionRepository: MissionRepository,
) {

    @DisplayName("미션 식별자로 미션 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val mission = createAndSaveMission()

        // when
        val result = missionRepository.find(mission.id!!)

        // then
        assertThat(result).isEqualTo(mission)
    }

    @DisplayName("챌린지 id로 조회하면 일치하는 챌린지를 반환한다")
    @Test
    fun givenValid_whenFindInfoById_thenReturnMission() {
        // given
        val mission = createAndSaveMission()

        // when
        val result = mission.id?.let { missionRepository.findInfoById(it) }

        // then
        assertThat(result).isEqualTo(mission)
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
}