package com.alloon.alloonserver.api.controller.user

import com.alloon.alloonserver.api.controller.user.response.UserChallengeHistoryResponse
import com.alloon.alloonserver.api.controller.user.response.UserInfoResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.ApiErrorResponses
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.alloon.alloonserver.service.user.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
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
    @Operation(summary = "사용자 정보 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND])
    fun findUserInfo(principal: Principal): ResponseEntity<UserInfoResponse> {
        val user = userService.findUserInfo(UserUtility.getUserId(principal))
        val response = UserInfoResponse.of(user)

        return ResponseEntity.ok(response)
    }

    @PostMapping("/api/users/image", consumes = [MULTIPART_FORM_DATA_VALUE])
    @Operation(
        summary = "사용자 프로필 이미지 업로드",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND, FILE_SIZE_EXCEED, IMAGE_TYPE_UNSUPPORTED])
    fun updateUserImage(
        principal: Principal,
        @RequestPart imageFile: MultipartFile
    ): ResponseEntity<UserInfoResponse> {
        val user = userService.updateUserImage(UserUtility.getUserId(principal), imageFile)
        val response = UserInfoResponse.of(user)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/users/challenge-history")
    @Operation(summary = "사용자 챌린지 기록 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND])
    fun findUserChallengeHistory(principal: Principal): ResponseEntity<UserChallengeHistoryResponse> {
        val challengeHistory = userService.findUserChallengeHistory(UserUtility.getUserId(principal))
        val response = UserChallengeHistoryResponse.of(challengeHistory)

        return ResponseEntity.ok(response)
    }
}