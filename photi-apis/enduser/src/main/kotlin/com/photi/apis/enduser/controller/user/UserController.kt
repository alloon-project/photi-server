package com.photi.apis.enduser.controller.user

import com.photi.apis.enduser.common.exception.annotation.ChallengeApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.ChallengeMemberApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.GlobalApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.UserApiErrorResponses
import com.photi.apis.enduser.common.success.dto.CollectionSuccessResponse
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.user.dto.request.FindImagePreSignedUrlRequest
import com.photi.apis.enduser.controller.user.dto.request.UpdateProfileImageRequest
import com.photi.apis.enduser.controller.user.dto.response.*
import com.photi.core.domain.challenge.exception.ChallengeErrorCode.CHALLENGE_NOT_FOUND
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
import com.photi.core.domain.user.service.UserService
import com.photi.utils.UserUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate

@Validated
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "사용자 API")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    @Operation(summary = "사용자 정보 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findInfo(principal: Principal): ResponseEntity<FindInfoResponse> {
        val info = userService.findInfo(UserUtil.getUserId(principal))
        val response = FindInfoResponse.of(info)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image/pre-signed-url")
    @Operation(summary = "이미지 PresignedURL 조회")
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([FILE_SIZE_EXCEED, IMAGE_TYPE_UNSUPPORTED])
    fun findImagePreSignedUrl(
        @RequestBody @Valid request: FindImagePreSignedUrlRequest,
    ): ResponseEntity<FindImagePreSignedUrlResponse> {
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun updateProfileImage(
        principal: Principal,
        @RequestBody @Valid request: UpdateProfileImageRequest,
    ): ResponseEntity<StringSuccessResponse> {
        userService.updateProfileImage(UserUtil.getUserId(principal), request.toServiceDto())
        return ResponseEntity.ok(StringSuccessResponse("사용자 프로필 이미지 업데이트가 완료되었습니다."))
    }

    @GetMapping("/challenge-history")
    @Operation(summary = "사용자 챌린지 기록 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findChallengeHistory(principal: Principal): ResponseEntity<FindChallengeHistoryResponse> {
        val challengeHistory = userService.findChallengeHistory(UserUtil.getUserId(principal))
        val response = FindChallengeHistoryResponse.of(challengeHistory)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/feeds")
    @Operation(
        summary = "사용자 피드 인증 날짜 리스트 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findFeedDates(principal: Principal): ResponseEntity<CollectionSuccessResponse> {
        val feedDates = userService.findFeedDates(UserUtil.getUserId(principal))
        return ResponseEntity.ok(CollectionSuccessResponse(feedDates))
    }

    @GetMapping("/challenges")
    @Operation(
        summary = "사용자 참여 중인 챌린지 갯수 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    fun findChallengeCount(principal: Principal): ResponseEntity<FindChallengeCountResponse> {
        val challengeCount = userService.findChallengeCount(UserUtil.getUserId(principal))
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, DATE_FORMAT_INVALID])
    fun findFeedsByDate(
        principal: Principal,
        @RequestParam @Parameter(description = "인증 날짜", example = "2024-10-23") date: LocalDate,
    ): ResponseEntity<List<FindFeedsByDateResponse>> {
        val feeds = userService.findFeedsByDate(UserUtil.getUserId(principal), date)
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findFeedHistory(
        principal: Principal,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindFeedHistoryResponse>> {
        val feedHistory = userService.findFeedHistory(UserUtil.getUserId(principal), page, size)
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findEndedChallenges(
        principal: Principal,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindEndedChallengesResponse>> {
        val endedChallenges =
            userService.findEndedChallenges(UserUtil.getUserId(principal), page, size)
        val response = SliceResponse.of(endedChallenges) { FindEndedChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/my-challenges")
    @Operation(
        summary = "사용자 참여 중인 챌린지 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "챌린지 인증시간이 빠른 순(ex. 4시 -> 18시 -> 20시)으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findChallenges(
        principal: Principal,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengesResponse>> {
        val challenges = userService.findChallenges(UserUtil.getUserId(principal), page, size)
        val response = SliceResponse.of(challenges) { FindChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/challenges/{challengeId}/prove")
    @Operation(
        summary = "사용자 챌린지 피드 당일 인증 여부 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    fun findChallengeIsProve(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeIsProveResponse> {
        val challengeIsProve =
            userService.findChallengeIsProve(UserUtil.getUserId(principal), challengeId)
        val response = FindChallengeIsProveResponse.of(challengeIsProve)
        return ResponseEntity.ok(response)
    }
}
