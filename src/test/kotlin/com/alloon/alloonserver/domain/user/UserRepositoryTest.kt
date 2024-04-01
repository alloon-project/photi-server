package com.alloon.alloonserver.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

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

    @DisplayName("이메일로 회원 조회를 하면 정상 작동한다")
    @Test
    fun givenValid_whenFindFetchContact_thenReturn() {
        // given
        val contact = createAndSaveContact()
        contact.verify(contact.verificationCode)
        val user = createAndSaveUser(contact)

        // when
        val foundUser = userRepository.findFetchContact(contact.email)

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

    private fun createAndSaveContact(): Contact {
        val contact = Contact(email = "tester@alloon.com", verificationCode = "000000")

        return contactRepository.save(contact)
    }

    private fun createAndSaveUser(contact: Contact): User {
        val user = User(contact = contact, username = "tester", password = "password")
        return userRepository.save(user)
    }
}