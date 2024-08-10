package com.alloon.alloonserver.domain.mission

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
class MissionMemberRepositoryTest(
    @Autowired private val missionMemberRepository: MissionMemberRepository,
    @Autowired private val missionRepository: MissionRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("미션 멤버 식별자로 미션 멤버 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val missionMember = createAndSaveMissionMember()

        // when
        val result = missionMemberRepository.find(missionMember.id!!)

        // then
        assertThat(result).isEqualTo(result)
    }

    @DisplayName("사용자 id와 챌린지 id로 조회하면 일치하는 챌린지 멤버를 반환한다")
    @Test
    fun givenValid_whenFindByUserIdAndMissionId_thenReturnMissionMember() {
        // given
        val missionMember = createAndSaveMissionMember()
        val userId = missionMember.user?.id
        val missionId = missionMember.mission.id

        // when
        val result = userId?.let { uid ->
            missionId?.let { mid ->
                missionMemberRepository.findByUserIdAndMissionId(uid, mid)
            }
        }

        // then
        assertThat(result).isEqualTo(missionMember)
    }

    private fun createAndSaveMissionMember(): MissionMember {
        val mission = createAndSaveMission()
        val contact = contactRepository.save(
            Contact(
                email = "tester@photi.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )
        val user = userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = "password1!",
                imageUrl = ""
            )
        )

        return missionMemberRepository.save(MissionMember(user = user, mission = mission))
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