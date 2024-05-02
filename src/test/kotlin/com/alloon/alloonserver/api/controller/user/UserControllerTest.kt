package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.service.user.UserService
import com.alloon.alloonserver.api.service.user.response.UserGetInfoResponse
import com.alloon.alloonserver.api.service.user.response.UserUploadImageResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
            .thenReturn(UserGetInfoResponse(1, "tester", "", "tester@alloon.com"))

        // when & then
        mockMvc.perform(
            get("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user/get-my-info",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING)
                            .description("회원 아이디"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("프로필 이미지"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                            .description("이메일"),
                    )
                )
            )
    }

    @DisplayName("회원 이미지 업로드를 하면 200을 반환한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn200() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        `when`(userService.uploadImage(anyLong(), any()))
            .thenReturn(UserUploadImageResponse(1, "tester", "https://www.google.com",
                "tester@alloon.com"))

        // when & then
        mockMvc.perform(
            post("/api/users/image")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .param("file", file.toString())
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "user/upload-image",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                            .description("회원 식별자"),
                        fieldWithPath("data.username").type(JsonFieldType.STRING)
                            .description("회원 아이디"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
                            .description("프로필 이미지"),
                        fieldWithPath("data.email").type(JsonFieldType.STRING)
                            .description("이메일"),
                    )
                )
            )
    }
}