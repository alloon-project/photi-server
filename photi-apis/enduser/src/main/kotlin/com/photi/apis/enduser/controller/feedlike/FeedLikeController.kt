package com.photi.apis.enduser.controller.feedlike

import com.photi.apis.enduser.common.exception.annotation.ChallengeMemberApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.FeedApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.FeedLikeApiErrorResponses
import com.photi.apis.enduser.common.exception.annotation.GlobalApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.getUserId
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.feed.exception.FeedErrorCode.FEED_NOT_FOUND
import com.photi.core.domain.feedlike.exception.FeedLikeErrorCode.EXISTING_FEED_LIKE
import com.photi.core.domain.feedlike.exception.FeedLikeErrorCode.FEED_LIKE_NOT_FOUND
import com.photi.core.domain.feedlike.service.FeedLikeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v2/feed-likes")
@Tag(name = "FeedLike", description = "피드 좋아요 API")
class FeedLikeController(
    private val feedLikeService: FeedLikeService,
) {

    @PostMapping("/{challengeId}/{feedId}")
    @Operation(summary = "피드 좋아요", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    @FeedLikeApiErrorResponses([EXISTING_FEED_LIKE])
    fun feedLike(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        feedLikeService.feedLike(user.getUserId(), challengeId, feedId)
        return ResponseEntity.ok(StringSuccessResponse("피드 좋아요가 완료되었습니다."))
    }

    @DeleteMapping("/{challengeId}/{feedId}")
    @Operation(summary = "피드 좋아요 취소", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    @FeedLikeApiErrorResponses([FEED_LIKE_NOT_FOUND])
    fun cancelFeedLike(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        feedLikeService.cancelFeedLike(user.getUserId(), challengeId, feedId)
        return ResponseEntity.ok(StringSuccessResponse("피드 좋아요가 취소되었습니다."))
    }
}
