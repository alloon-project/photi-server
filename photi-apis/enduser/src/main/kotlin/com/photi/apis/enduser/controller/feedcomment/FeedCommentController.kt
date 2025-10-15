package com.photi.apis.enduser.controller.feedcomment

import com.photi.apis.enduser.common.exception.annotation.*
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.feedcomment.dto.request.CreateChallengeFeedCommentRequest
import com.photi.apis.enduser.controller.feedcomment.dto.response.FindFeedCommentsResponse
import com.photi.apis.enduser.controller.feedcomment.dto.response.RegisterFeedCommentResponse
import com.photi.core.domain.challenge.exception.ChallengeErrorCode.CHALLENGE_NOT_FOUND
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.TOKEN_UNAUTHENTICATED
import com.photi.core.domain.common.exception.GlobalErrorCode.TOKEN_UNAUTHORIZED
import com.photi.core.domain.feed.exception.FeedErrorCode.FEED_NOT_FOUND
import com.photi.core.domain.feedcomment.exception.FeedCommentErrorCode.FEED_COMMENT_NOT_FOUND
import com.photi.core.domain.feedcomment.service.FeedCommentService
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
@RequestMapping("/api/v1")
@Tag(name = "FeedComment", description = "피드 댓글 API")
class FeedCommentController(
    private val feedCommentService: FeedCommentService,
) {

    @PostMapping("/challenges/{challengeId}/feeds/{feedId}/comments")
    @Operation(summary = "피드 댓글 등록", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    fun registerFeedComment(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @RequestBody @Valid request: CreateChallengeFeedCommentRequest,
    ): ResponseEntity<RegisterFeedCommentResponse> {
        val feedComment = feedCommentService.registerFeedComment(
            UserUtil.getUserId(principal),
            challengeId,
            feedId,
            request.toServiceDto(),
        )
        val response = RegisterFeedCommentResponse.of(feedComment)
        return ResponseEntity.status(CREATED).body(response)
    }

    @DeleteMapping("/challenges/{challengeId}/feeds/{feedId}/comments/{commentId}")
    @Operation(summary = "피드 댓글 삭제", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    @FeedCommentApiErrorResponses([FEED_COMMENT_NOT_FOUND])
    fun deleteFeedComment(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @PathVariable @Parameter(description = "댓글 id", example = "1") commentId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        feedCommentService.deleteFeedComment(
            UserUtil.getUserId(principal),
            challengeId,
            feedId,
            commentId,
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 피드 댓글 삭제가 완료되었습니다."))
    }

    @GetMapping("/feeds/{feedId}/comments")
    @Operation(
        summary = "피드 댓글 리스트 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "댓글 작성 최신순으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findFeedComments(
        principal: Principal,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindFeedCommentsResponse>> {
        val feedComments = feedCommentService.findFeedComments(feedId, page, size)
        val response = SliceResponse.of(feedComments) { FindFeedCommentsResponse.of(it) }
        return ResponseEntity.ok(response)
    }
}
