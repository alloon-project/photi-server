package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.service.user.UserService
import com.alloon.alloonserver.api.service.user.response.UserGetInfoResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : RestDocsSupport() {

    private val userService = mock(UserService::class.java)

    override fun initController(): Any {
        return UserController(userService)
    }

    @DisplayName("내 회원 정보를 조회하면 200을 반환한다")
    @Test
    fun givenValid_whenGetMyInfo_thenReturn200() {
        // given
        `when`(userService.getInfo(anyLong()))
            .thenReturn(UserGetInfoResponse(1, "tester", null, "tester@alloon.com"))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/users")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "user/get-my-info",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        PayloadDocumentation.fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"),
                        PayloadDocumentation.fieldWithPath("data.username").type(JsonFieldType.STRING)
                            .description("회원 아이디"),
                        PayloadDocumentation.fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).optional()
                            .description("프로필 이미지"),
                        PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING)
                            .description("이메일"),
                    )
                )
            )
    }


}