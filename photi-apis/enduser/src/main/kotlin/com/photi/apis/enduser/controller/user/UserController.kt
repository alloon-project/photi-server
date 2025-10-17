package com.photi.apis.enduser.controller.user

import com.photi.apis.enduser.common.exception.annotation.GlobalApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.UserApiErrorResponses
import com.photi.apis.enduser.common.success.dto.CollectionSuccessResponse
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.user.dto.request.FindImagePreSignedUrlRequest
import com.photi.apis.enduser.controller.user.dto.request.UpdateProfileImageRequest
import com.photi.apis.enduser.controller.user.dto.response.*
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
import com.photi.core.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Validated
@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User", description = "사용자 API")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @Operation(summary = "사용자 정보 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findInfo(@AuthUser user: CustomUserDetails): ResponseEntity<FindInfoResponse> {
        val info = userService.findInfo(user.getUserId())
        val response = FindInfoResponse.of(info)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image/pre-signed-url")
    @Operation(summary = "이미지 PresignedURL 조회")
    @ApiResponse(responseCode = "200")
    fun findImagePreSignedUrl(@RequestBody @Valid request: FindImagePreSignedUrlRequest): ResponseEntity<FindImagePreSignedUrlResponse> {
        val preSignedUrl = userService.findImagePreSignedUrl(request.toServiceDto())
        val response = FindImagePreSignedUrlResponse.of(preSignedUrl)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/image")
    @Operation(
        summary = "사용자 프로필 이미지 업데이트",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun updateProfileImage(
        @AuthUser user: CustomUserDetails,
        @RequestBody @Valid request: UpdateProfileImageRequest,
    ): ResponseEntity<StringSuccessResponse> {
        userService.updateProfileImage(user.getUserId(), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("사용자 프로필 이미지 업데이트가 완료되었습니다."))
    }

    @GetMapping("/challenge-history")
    @Operation(summary = "사용자 챌린지 기록 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findChallengeHistory(@AuthUser user: CustomUserDetails): ResponseEntity<FindChallengeHistoryResponse> {
        val challengeHistory = userService.findChallengeHistory(user.getUserId())
        val response = FindChallengeHistoryResponse.of(challengeHistory)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/feed-dates")
    @Operation(
        summary = "사용자 피드 인증 날짜 리스트 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findFeedDates(@AuthUser user: CustomUserDetails): ResponseEntity<CollectionSuccessResponse> {
        val feedDates = userService.findFeedDates(user.getUserId())
        return ResponseEntity.ok(CollectionSuccessResponse(feedDates))
    }

    @GetMapping("/challenge-count")
    @Operation(
        summary = "사용자 참여 중인 챌린지 갯수 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findChallengeCount(@AuthUser user: CustomUserDetails): ResponseEntity<FindChallengeCountResponse> {
        val challengeCount = userService.findChallengeCount(user.getUserId())
        val response = FindChallengeCountResponse.of(challengeCount)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/feeds-by-date")
    @Operation(
        summary = "사용자 피드 인증 개별 날짜 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "피드 인증 시간 빠른순으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN, DATE_FORMAT_INVALID])
    fun findFeedsByDate(
        @AuthUser user: CustomUserDetails,
        @RequestParam @Parameter(description = "인증 날짜", example = "2024-10-23") date: LocalDate,
    ): ResponseEntity<List<FindFeedsByDateResponse>> {
        val feeds = userService.findFeedsByDate(user.getUserId(), date)
        val response = FindFeedsByDateResponse.of(feeds)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/feed-history")
    @Operation(
        summary = "사용자 피드 인증 횟수 모아보기 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "피드 인증 날짜 최신순으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findFeedHistory(
        @AuthUser user: CustomUserDetails,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindFeedHistoryResponse>> {
        val feedHistory = userService.findFeedHistory(user.getUserId(), page, size)
        val response = SliceResponse.of(feedHistory) { FindFeedHistoryResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/ended-challenges")
    @Operation(
        summary = "사용자 종료된 챌린지 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "챌린지 종료 날짜 최신순으로 정렬되어 조회됩니다. 챌린지 파티원 이미지는 최근 가입순으로 최대 3개 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findEndedChallenges(
        @AuthUser user: CustomUserDetails,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindEndedChallengesResponse>> {
        val endedChallenges = userService.findEndedChallenges(user.getUserId(), page, size)
        val response = SliceResponse.of(endedChallenges) { FindEndedChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/challenges")
    @Operation(
        summary = "사용자 참여 중인 챌린지 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "챌린지 인증시간이 빠른 순(ex. 4시 -> 18시 -> 20시)으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findChallenges(
        @AuthUser user: CustomUserDetails,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengesResponse>> {
        val challenges = userService.findChallenges(user.getUserId(), page, size)
        val response = SliceResponse.of(challenges) { FindChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/prove")
    @Operation(
        summary = "사용자 챌린지 피드 당일 인증 여부 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findChallengeIsProve(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeIsProveResponse> {
        val challengeIsProve = userService.findChallengeIsProve(user.getUserId(), challengeId)
        val response = FindChallengeIsProveResponse.of(challengeIsProve)
        return ResponseEntity.ok(response)
    }
}
