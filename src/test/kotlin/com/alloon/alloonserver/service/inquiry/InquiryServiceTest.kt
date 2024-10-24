package com.alloon.alloonserver.service.inquiry

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.inquiry.InquiryCategoryType
import com.alloon.alloonserver.domain.inquiry.InquiryRepository
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.inquiry.dto.CreateInquiryDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional

@Transactional
class InquiryServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val inquiryRepository = mockk<InquiryRepository>()

    private val inquiryService = InquiryService(userRepository, inquiryRepository)

    @DisplayName("문의하기를 하면 문의 내용이 저장된다.")
    @Test
    fun givenValid_whenCreateInquiry_thenReturn() {
        // given
        val userId = 1L
        val dto = CreateInquiryDto("SERVICE_USE", "서비스 이용 관련 문의입니다.")
        val user = getUser()
        val inquiry = dto.toEntity(user)

        every { userRepository.find(any()) } returns user
        every { inquiryRepository.save(any()) } returns inquiry

        // when
        inquiryService.createInquiry(userId, dto)

        // then
        assertThat(inquiry.type).isEqualTo(InquiryCategoryType.valueOf(dto.type))
        assertThat(inquiry.content).isEqualTo(dto.content)
    }

    @DisplayName("등록되지 않은 사용자가 문의하기를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenCreateInquiry_thenThrow() {
        // given
        val userId = 1L
        val dto = CreateInquiryDto("SERVICE_USE", "서비스 이용 관련 문의입니다.")

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { inquiryService.createInquiry(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }
}