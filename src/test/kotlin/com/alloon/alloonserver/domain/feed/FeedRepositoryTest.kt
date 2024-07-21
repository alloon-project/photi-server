package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.MissionMemberRepository
import com.alloon.alloonserver.domain.mission.MissionRepository
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class FeedRepositoryTest(
    @Autowired private val feedRepository: FeedRepository,
    @Autowired private val missionRepository: MissionRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val missionMemberRepository: MissionMemberRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @DisplayName("피드 식별자로 피드 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val feed = createAdnSaveFeed()

        // when
        val result = feedRepository.find(feed.id!!)

        // then
        assertThat(result).isEqualTo(feed)
    }


    private fun createAdnSaveFeed(): Feed {
        val mission = missionRepository.save(
            Mission(
                missionName = "미션명", description = "미션 설명", goal = "미션 목표",
                imageUrl = "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
                endDate = LocalDate.of(2999, 1, 1),
                hashtags = listOf("러닝")
            )
        )

        val contact = contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )
        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = encryptedPassword,
                imageUrl = ""
            )
        )
        val missionMember =
            missionMemberRepository.save(MissionMember(user = user, mission = mission, creatorYn = true))

        return feedRepository.save(Feed(missionMember = missionMember, mission = mission, imageUrl = ""))
    }
}