package com.photi.apis.enduser.controller.feed

import com.photi.apis.enduser.common.exception.annotation.*
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.feed.dto.request.FindImagePreSignedUrlRequest
import com.photi.apis.enduser.controller.feed.dto.request.RegisterFeedRequest
import com.photi.apis.enduser.controller.feed.dto.response.*
import com.photi.core.domain.challenge.exception.ChallengeErrorCode.CHALLENGE_NOT_FOUND
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.feed.exception.FeedErrorCode.*
import com.photi.core.domain.feed.model.SortType
import com.photi.core.domain.feed.service.FeedService
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v2/feeds")
@Tag(name = "Feed", description = "피드 API")
class FeedController(
    private val feedService: FeedService,
) {

    @PostMapping("/image/pre-signed-url")
    @Operation(summary = "이미지 PresignedURL 조회")
    @ApiResponse(responseCode = "200")
    fun findImagePreSignedUrl(@RequestBody @Valid request: FindImagePreSignedUrlRequest): ResponseEntity<FindImagePreSignedUrlResponse> {
        val preSignedUrl = feedService.findImagePreSignedUrl(request.toServiceDto())
        val response = FindImagePreSignedUrlResponse.of(preSignedUrl)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{challengeId}")
    @Operation(summary = "피드 인증", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([EXISTING_FEED])
    fun registerFeed(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: RegisterFeedRequest,
    ): ResponseEntity<StringSuccessResponse> {
        feedService.registerFeed(user.getUserId(), challengeId, request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("피드 인증이 완료되었습니다."))
    }

    @DeleteMapping("/{challengeId}/{feedId}")
    @Operation(summary = "피드 삭제", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_CREATOR_FORBIDDEN])
    fun deleteFeed(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        feedService.deleteFeed(user.getUserId(), challengeId, feedId)
        return ResponseEntity.ok(StringSuccessResponse("피드 삭제가 완료되었습니다."))
    }

    @GetMapping("/{challengeId}")
    @Operation(
        summary = "피드 조회",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findFeeds(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortType,
    ): ResponseEntity<SliceResponse<FindFeedsResponse>> {
        val feeds = feedService.findFeeds(user.getUserId(), challengeId, page, size, sort)
        val response = SliceResponse.of(feeds) { FindFeedsResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/v2")
    @Operation(
        summary = "피드 조회 v2",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findFeedsV2(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortType,
    ): ResponseEntity<SliceResponse<FindFeedsV2Response>> {
        val feeds = feedService.findFeedsV2(user.getUserId(), challengeId, page, size, sort)
        val response = SliceResponse.of(feeds) { FindFeedsV2Response.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/{feedId}")
    @Operation(summary = "피드 개별 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    fun findFeed(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<FindFeedResponse> {
        val feed = feedService.findFeed(user.getUserId(), challengeId, feedId)
        val response = FindFeedResponse.of(feed)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/member-count")
    @Operation(
        summary = "피드 당일 인증 파티원 수 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    fun findTodayFeedMemberCount(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindTodayFeedMemberCountResponse> {
        val count = feedService.findTodayFeedMemberCount(challengeId)
        val response = FindTodayFeedMemberCountResponse.of(count)
        return ResponseEntity.ok(response)
    }
}
