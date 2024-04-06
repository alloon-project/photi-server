package com.alloon.alloonserver.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRepositoryTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
) {

    @DisplayName("연락처로 회원 존재 여부 확인이 정상 작동한다")
    fun givenValid_whenExistsByContact_thenReturn() {
        // given
        val contact = createAndSaveContact()
        createAndSaveContact()

        // when
        val result = userRepository.existsByContact(contact)

        // then
        assertThat(result).isTrue()
    }

    @DisplayName("아이디로 회원 존재 여부 확인이 정상 작동한다")
    @Test
    fun givenValid_whenExistsByUsername_thenReturn() {
        // given
        val username = "tester"

        // when
        val result = userRepository.existsByUsername(username)

        // then
        assertThat(result).isFalse()
    }

    @ParameterizedTest(name = "[{index}] email={0}, username={1}, userId={2}으로 회원 조회를 하면 정상 작동한다")
    @MethodSource("providerFindFetchContact")
    @DisplayName("회원 조회를 하면 정상 작동한다")
    fun givenValid_whenFindFetchContact_thenReturn(useEmail: Boolean, useUsername: Boolean, useUserId: Boolean) {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)

        val email: String? = contact.email.takeIf { useEmail }
        val username: String? = user.username.takeIf { useUsername }
        val userId: Long? = user.id.takeIf { useUserId }

        // when
        val foundUser = userRepository.findFetchContact(email, username, userId)

        assertAll(
            {
                assertThat(foundUser)
                    .extracting("username", "password", "imageUrl", "isTemporaryPassword", "createdDateTime",
                        "updatedDateTime", "contact")
                    .containsExactly(user.username, user.password, user.imageUrl, user.isTemporaryPassword,
                        user.createdDateTime, user.updatedDateTime, user.contact)
            },
            {
                assertThat(foundUser)
                    .extracting("contact")
                    .extracting("email", "verificationCode", "isVerified", "createdDateTime", "updatedDateTime")
                    .containsExactly(contact.email, contact.verificationCode, contact.isVerified,
                        contact.createdDateTime, contact.updatedDateTime)
            }
        )
    }

    @DisplayName("회원 식별자로 회원 조회를 하면 정상 작동한다")
    @Test
    fun givenValidUserId_whenFind_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)

        // when
        val foundUser = userRepository.find(user.id!!)

        // then
        assertThat(foundUser)
            .extracting("username", "password", "imageUrl", "isTemporaryPassword", "createdDateTime", "updatedDateTime")
            .containsExactly(user.username, user.password, user.imageUrl, user.isTemporaryPassword,
                user.createdDateTime, user.updatedDateTime)
    }

    private fun createAndSaveContact(): Contact {
        val contact = Contact(email = "tester@alloon.com", verificationCode = "000000")

        return contactRepository.save(contact)
    }

    private fun createAndSaveUser(contact: Contact): User {
        val user = User(contact = contact, username = "tester", password = "password")
        return userRepository.save(user)
    }

    companion object {
        @JvmStatic
        private fun providerFindFetchContact(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(true, false, false),
                Arguments.of(false, true, false),
                Arguments.of(false, false, true),
                Arguments.of(true, true, false),
                Arguments.of(true, false, true),
                Arguments.of(false, true, true),
                Arguments.of(true, true, true)
            )
        }
    }
}