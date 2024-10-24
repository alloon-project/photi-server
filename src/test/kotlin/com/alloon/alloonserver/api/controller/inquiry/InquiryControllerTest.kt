package com.alloon.alloonserver.api.controller.inquiry

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.inquiry.request.CreateInquiryRequest
import com.alloon.alloonserver.service.inquiry.InquiryService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InquiryControllerTest : RestDocsSupport() {

    private val inquiryService = mockk<InquiryService>()

    override fun initController(): Any {
        return InquiryController(inquiryService)
    }

    @DisplayName("문의하기를 성공하면 201을 반환한다.")
    @Test
    fun givenValid_whenCreateInquiry_thenReturn201() {
        // given
        val request = CreateInquiryRequest("SERVICE_USE", "서비스 이용 관련 문의입니다.")

        every { inquiryService.createInquiry(any(), any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            post("/api/inquiries")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isCreated)
    }
}