package com.photi.apis.enduser.controller.feed

import com.photi.apis.enduser.common.exception.annotation.*
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
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
import com.photi.utils.UserUtil
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
import java.security.Principal

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Feed", description = "피드 API")
class FeedController(
    private val feedService: FeedService,
) {

    @PostMapping("/image/pre-signed-url")
    @Operation(summary = "이미지 PresignedURL 조회")
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([FILE_SIZE_EXCEED, IMAGE_TYPE_UNSUPPORTED])
    fun findImagePreSignedUrl(
        @RequestBody @Valid request: FindImagePreSignedUrlRequest,
    ): ResponseEntity<FindImagePreSignedUrlResponse> {
        val preSignedUrl = feedService.findImagePreSignedUrl(request.toServiceDto())
        val response = FindImagePreSignedUrlResponse.of(preSignedUrl)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/challenges/{challengeId}/feeds")
    @Operation(summary = "피드 인증", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, FILE_SIZE_EXCEED, IMAGE_TYPE_UNSUPPORTED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([EXISTING_FEED])
    fun registerFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: RegisterFeedRequest,
    ): ResponseEntity<StringSuccessResponse> {
        feedService.registerFeed(UserUtil.getUserId(principal), challengeId, request.toServiceDto())
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("피드 인증이 완료되었습니다."))
    }

    @DeleteMapping("/challenges/{challengeId}/feeds/{feedId}")
    @Operation(summary = "피드 삭제", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_CREATOR_FORBIDDEN])
    fun deleteFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        feedService.deleteFeed(UserUtil.getUserId(principal), challengeId, feedId)
        return ResponseEntity.ok(StringSuccessResponse("피드 삭제가 완료되었습니다."))
    }

    @GetMapping("/challenges/{challengeId}/feeds")
    @Operation(
        summary = "피드 조회",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findFeeds(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortType,
    ): ResponseEntity<SliceResponse<FindFeedsResponse>> {
        val feeds =
            feedService.findFeeds(UserUtil.getUserId(principal), challengeId, page, size, sort)
        val response = SliceResponse.of(feeds) { FindFeedsResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/challenges/{challengeId}/feeds/v2")
    @Operation(
        summary = "피드 조회 v2",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findFeedsV2(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortType,
    ): ResponseEntity<SliceResponse<FindFeedsV2Response>> {
        val feeds =
            feedService.findFeedsV2(UserUtil.getUserId(principal), challengeId, page, size, sort)
        val response = SliceResponse.of(feeds) { FindFeedsV2Response.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/challenges/{challengeId}/feeds/{feedId}")
    @Operation(summary = "피드 개별 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    fun findFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<FindFeedResponse> {
        val feed = feedService.findFeed(UserUtil.getUserId(principal), challengeId, feedId)
        val response = FindFeedResponse.of(feed)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/challenges/{challengeId}/feed-members")
    @Operation(
        summary = "피드 당일 인증 파티원 수 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    fun findTodayFeedMemberCount(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindTodayFeedMemberCountResponse> {
        val count = feedService.findTodayFeedMemberCount(challengeId)
        val response = FindTodayFeedMemberCountResponse.of(count)
        return ResponseEntity.ok(response)
    }
}
