package com.alloon.alloonserver.domain.mission

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@Transactional
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

    private fun createAndSaveMission(): Mission {
        return missionRepository.save(Mission(missionName = "미션명", description = "미션 설명",
            imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            endDate = LocalDate.of(2999, 1, 1)))
    }
}