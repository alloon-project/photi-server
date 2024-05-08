package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
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
class MissionMemberRepositoryTest(
    @Autowired private val missionMemberRepository: MissionMemberRepository,
    @Autowired private val missionRepository: MissionRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val passwordUtility: PasswordUtility,
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

    private fun createAndSaveMissionMember(): MissionMember {
        val mission = missionRepository.save(Mission(missionName = "미션명", description = "미션 설명", goal = "미션 목표",
            imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            endDate = LocalDate.of(2999, 1, 1)))

        val contact = contactRepository.save(Contact(
            email = "tester@alloon.com",
            verificationCode = "000000",
            verifyYn = true
        ))

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = ""))

        return missionMemberRepository.save(MissionMember(user = user, mission = mission, creatorYn = true))
    }
}