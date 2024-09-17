package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.service.user.UserService
import com.alloon.alloonserver.service.user.dto.FindUserInfoDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest : RestDocsSupport() {

    private val userService = mockk<UserService>()

    override fun initController(): Any {
        return UserController(userService)
    }

    @DisplayName("사용자 정보 조회를 성공하면 200을 반환한다")
    @Test
    fun givenValid_whenFindUserInfo_thenReturn200() {
        // given
        val dto = FindUserInfoDto("https://url.kr/5MhHhD", "tester", "tester@photi.com")

        every { userService.findUserInfo(any()) } returns dto

        // when
        val resultActions = mockMvc.perform(
            get("/api/users")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        )

        // then
        resultActions.andExpect(status().isOk)
    }

//    @DisplayName("회원 이미지 업로드를 하면 200을 반환한다")
//    @Test
//    fun givenValid_whenUploadImage_thenReturn200() {
//        // given
//        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
//
//        `when`(userService.uploadImage(anyLong(), any()))
//            .thenReturn(
//                UserUploadImageResponse(
//                    1, "tester", "https://www.google.com",
//                    "tester@alloon.com"
//                )
//            )
//
//        // when & then
//        mockMvc.perform(
//            multipart("/api/users/image")
//                .file(MockMultipartFile("file", "file.png", "image/png", ByteArray(1)))
//                .header(AUTHORIZATION, "Bearer access-token")
//                .principal(mockPrincipal)
//                .contentType(MULTIPART_FORM_DATA_VALUE)
//        ).andDo(print()).andExpect(status().isOk)
//    }
}