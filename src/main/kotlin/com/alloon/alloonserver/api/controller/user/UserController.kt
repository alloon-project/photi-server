package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.service.user.UserService
import com.alloon.alloonserver.api.service.user.response.UserGetInfoResponse
import com.alloon.alloonserver.api.service.user.response.UserUploadImageResponse
import com.alloon.alloonserver.common.constant.SuccessCode
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MY_USER_INFO
import com.alloon.alloonserver.common.response.DefaultSingleResponse
import com.alloon.alloonserver.common.util.UserUtility
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Validated
@RestController
@Tag(name = "User", description = "사용자 API")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/api/users")
    @Operation(summary = "사용자 정보 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용자 정보 조회 성공",
                content = [Content(schema = Schema(implementation = UserGetInfoResponse::class))]
            )
        ]
    )
    fun getMyInfo(principal: Principal): ResponseEntity<DefaultSingleResponse> {
        val response = userService.getInfo(UserUtility.getUserId(principal))

        return DefaultSingleResponse.toResponseEntity(FOUND_MY_USER_INFO, response)
    }

    @PostMapping("/api/users/image", consumes = [MULTIPART_FORM_DATA_VALUE], produces = [APPLICATION_JSON_VALUE])
    @Operation(summary = "사용자 프로필 이미지 업로드")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용자 프로필 이미지 업로드 성공",
                content = [Content(schema = Schema(implementation = UserUploadImageResponse::class))]
            )
        ]
    )
    fun uploadImage(principal: Principal, @RequestPart(required = false) file: MultipartFile?):
            ResponseEntity<DefaultSingleResponse> {
        val response = userService.uploadImage(UserUtility.getUserId(principal), file)

        return DefaultSingleResponse.toResponseEntity(SuccessCode.USER_IMAGE_UPLOADED, response)
    }
}