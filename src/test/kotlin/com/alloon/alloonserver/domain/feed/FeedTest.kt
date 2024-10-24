package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.service.challenge.dto.ChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.ChallengeRuleDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class FeedTest {

    @DisplayName("피드 댓글 수가 증가한다.")
    @Test
    fun givenValid_whenUpdateCommentCnt_thenReturn() {
        // given
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(1L, getUser(getContact()), challenge)
        val feed = Feed(1L, challengeMember, challenge, imageUrl, 10, 5)

        // when
        feed.updateCommentCnt()

        // then
        assertThat(feed.commentCnt).isEqualTo(6)
    }

    @DisplayName("피드 댓글 수가 0보다 크면 피드 댓글 수가 감소한다.")
    @Test
    fun givenCommentCntGreaterThanZero_whenDecreaseCommentCnt_thenReturn() {
        // given
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(1L, getUser(getContact()), challenge)
        val feed = Feed(1L, challengeMember, challenge, imageUrl, 10, 5)

        // when
        feed.decreaseCommentCnt()

        // then
        assertThat(feed.commentCnt).isEqualTo(4)
    }

    @DisplayName("피드 댓글 수가 0이면 피드 댓글 수가 감소하지 않는다.")
    @Test
    fun givenCommentCntZero_whenDecreaseCommentCnt_thenReturn() {
        // given
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(1L, getUser(getContact()), challenge)
        val feed = Feed(1L, challengeMember, challenge, imageUrl, 10, 0)

        // when
        feed.decreaseCommentCnt()

        // then
        assertThat(feed.commentCnt).isEqualTo(0)
    }

    private fun getUser(contact: Contact): User {
        return User(
            contact = contact,
            username = "tester",
            password = "password1!",
            imageUrl = "",
            feedCnt = 4
        )
    }

    private fun getContact(): Contact {
        return Contact(email = "tester@photi.com", verificationCode = "000000")
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