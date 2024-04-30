package com.alloon.alloonserver.domain.user

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ContactRepositoryTest(
    @Autowired private val contactRepository: ContactRepository,
) {

    @DisplayName("이메일로 연락처 단건 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindByEmail_thenReturn() {
        // given
        val contact = createAndSaveContact()

        // when
        val foundContact = contactRepository.findByEmail(contact.email)

        // then
        assertThat(foundContact).isEqualTo(contact)
    }

    private fun createAndSaveContact(): Contact {
        val contact = Contact(email = "tester@alloon.com", verificationCode = "000000")

        return contactRepository.save(contact)
    }
}