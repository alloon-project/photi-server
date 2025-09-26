package com.photi.apis.enduser.controller.challenge

import com.photi.apis.enduser.common.exception.ApiErrorResponses
import com.photi.apis.enduser.common.success.dto.CollectionSuccessResponse
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.challenge.dto.request.*
import com.photi.apis.enduser.controller.challenge.dto.response.*
import com.photi.core.domain.challenge.usecase.ChallengeService
import com.photi.core.domain.common.consts.SortTypeConstants
import com.photi.core.domain.common.consts.SwaggerConstants.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.utils.UserUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Validated
@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
class ChallengeController(
    private val challengeService: ChallengeService,
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "챌린지 생성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.EMPTY_FILE_INVALID, ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.FILE_SIZE_EXCEED, ExceptionCode.IMAGE_TYPE_UNSUPPORTED, ExceptionCode.CHALLENGE_LIMIT_EXCEED])
    fun createChallenge(
        principal: Principal,
        @RequestPart @Valid request: CreateChallengeRequest,
        @RequestPart imageFile: MultipartFile,
    ): ResponseEntity<CreateChallengeResponse> {
        val challenge = challengeService.createChallenge(
            UserUtil.getUserId(principal),
            request.toServiceDto(),
            imageFile
        )
        val response = CreateChallengeResponse.of(challenge)
        return ResponseEntity.status(CREATED).body(response)
    }

    @GetMapping("/example-images")
    @Operation(summary = "챌린지 예시 이미지 리스트 조회")
    @ApiResponse(responseCode = "200")
    fun getChallengeExampleImages(): ResponseEntity<CollectionSuccessResponse> {
        val response = challengeService.getChallengeExampleImages()
        return ResponseEntity.ok(CollectionSuccessResponse(response))
    }

    @GetMapping("/popular")
    @Operation(
        summary = "지금 인기있는 챌린지 조회",
        description = "공개, 비공개 및 종료되지 않은 챌린지가 방문순으로 최대 5개 조회됩니다. 챌린지 파티원 이미지는 최근 가입순으로 최대 3개 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun findPopularChallenges(): ResponseEntity<List<FindPopularChallengesResponse>> {
        val response = FindPopularChallengesResponse.of(challengeService.findPopularChallenges())
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/info")
    @Operation(summary = "챌린지 소개 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND])
    fun findChallengeInfo(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeInfoResponse> {
        val challengeInfo = challengeService.findChallengeInfo(challengeId)
        val response = FindChallengeInfoResponse.of(challengeInfo)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{challengeId}/challenge-members/goal")
    @Operation(summary = "챌린지 개인목표 작성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND])
    fun updateChallengeMemberGoal(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: UpdateChallengeMemberGoalRequest,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.updateChallengeMemberGoal(
            UserUtil.getUserId(principal),
            challengeId,
            request.toServiceDto()
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 개인목표 작성이 완료되었습니다."))
    }

    @GetMapping("/{challengeId}/challenge-members")
    @Operation(
        summary = "챌린지 파티원 조회",
        description = "파티장 -> 본인 -> 가입순으로 파티원이 전체 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun findChallengeMembers(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<List<FindChallengeMembersResponse>> {
        val challengeMembers =
            challengeService.findChallengeMembers(UserUtil.getUserId(principal), challengeId)
        val response = FindChallengeMembersResponse.of(challengeMembers)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "모든 챌린지 조회", description = "종료 날짜 최신순으로 모든 챌린지가 조회됩니다.")
    @ApiResponse(responseCode = "200")
    fun findAllChallenges(
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengesResponse>> {
        val challenges = challengeService.findAllChallenges(page, size)
        val response = SliceResponse.of(challenges) { FindChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}")
    @Operation(summary = "챌린지 개별 조회", description = "챌린지 파티원 이미지는 최근 가입순으로 최대 3개 조회됩니다.")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.CHALLENGE_NOT_FOUND])
    fun findChallenge(
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeResponse> {
        val challenge = challengeService.findChallenge(challengeId)
        val response = FindChallengeResponse.of(challenge)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{challengeId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "챌린지 수정", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_CREATOR_FORBIDDEN, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.FILE_SIZE_EXCEED, ExceptionCode.IMAGE_TYPE_UNSUPPORTED])
    fun updateChallenge(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestPart @Valid request: UpdateChallengeRequest,
        @RequestPart imageFile: MultipartFile,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.updateChallenge(
            UserUtil.getUserId(principal),
            challengeId,
            request.toServiceDto(),
            imageFile
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 수정이 완료되었습니다."))
    }

    @DeleteMapping("/{challengeId}")
    @Operation(summary = "챌린지 탈퇴", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND])
    fun deleteChallenge(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.deleteChallenge(UserUtil.getUserId(principal), challengeId)
        return ResponseEntity.ok(StringSuccessResponse("챌린지 탈퇴가 완료되었습니다."))
    }

    @PostMapping("/{challengeId}/feeds", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "챌린지 피드 인증", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.EXISTING_FEED, ExceptionCode.FILE_SIZE_EXCEED, ExceptionCode.IMAGE_TYPE_UNSUPPORTED])
    fun createChallengeFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestPart imageFile: MultipartFile,
    ): ResponseEntity<CreateChallengeFeedResponse> {
        val feed = challengeService.createChallengeFeed(
            UserUtil.getUserId(principal),
            challengeId,
            imageFile
        )
        val response = CreateChallengeFeedResponse.of(feed)
        return ResponseEntity.status(CREATED).body(response)
    }

    @DeleteMapping("/{challengeId}/feeds/{feedId}")
    @Operation(summary = "챌린지 피드 삭제", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.FEED_CREATOR_FORBIDDEN])
    fun deleteChallengeFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.deleteChallengeFeed(UserUtil.getUserId(principal), challengeId, feedId)
        return ResponseEntity.ok(StringSuccessResponse("챌린지 피드 삭제가 완료되었습니다."))
    }

    @GetMapping("/{challengeId}/feeds")
    @Operation(
        summary = "챌린지 피드 조회",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun findChallengeFeeds(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortTypeConstants,
    ): ResponseEntity<SliceResponse<FindChallengeFeedsByDateResponse>> {
        val challengeFeeds = challengeService.findChallengeFeeds(
            UserUtil.getUserId(principal),
            challengeId,
            page,
            size,
            sort,
        )
        val response = SliceResponse.of(challengeFeeds) { FindChallengeFeedsByDateResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/feeds/v2")
    @Operation(
        summary = "챌린지 피드 조회 v2",
        description = "정렬 기준 선택 시, 최신순[피드 인증 날짜 최신순], 인기순[반응 수(하트 수 + 댓글 수) 많은 순]으로 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun findChallengeFeedsV2(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
        @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LATEST") sort: SortTypeConstants,
    ): ResponseEntity<SliceResponse<FindChallengeFeedsResponse>> {
        val challengeFeeds = challengeService.findChallengeFeedsV2(
            UserUtil.getUserId(principal),
            challengeId,
            page,
            size,
            sort
        )
        val response = SliceResponse.of(challengeFeeds) { FindChallengeFeedsResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/feed-members")
    @Operation(
        summary = "챌린지 피드 당일 인증 파티원 수 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND])
    fun findChallengeFeedMemberCnt(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeFeedMemberCntResponse> {
        val challenge = challengeService.findChallengeFeedMemberCnt(challengeId)
        val response = FindChallengeFeedMemberCntResponse.of(challenge)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{challengeId}/feeds/{feedId}/comments")
    @Operation(summary = "챌린지 피드 댓글 등록", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND])
    fun createChallengeFeedComment(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @RequestBody @Valid request: CreateChallengeFeedCommentRequest,
    ): ResponseEntity<CreateChallengeFeedCommentResponse> {
        val feedCommentId = challengeService.createChallengeFeedComment(
            UserUtil.getUserId(principal),
            challengeId,
            feedId,
            request.toServiceDto(),
        )
        val response = CreateChallengeFeedCommentResponse.of(feedCommentId)
        return ResponseEntity.status(CREATED).body(response)
    }

    @DeleteMapping("/{challengeId}/feeds/{feedId}/comments/{commentId}")
    @Operation(summary = "챌린지 피드 댓글 삭제", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND, ExceptionCode.FEED_COMMENT_NOT_FOUND])
    fun deleteChallengeFeedComment(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @PathVariable @Parameter(description = "댓글 id", example = "1") commentId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.deleteChallengeFeedComment(
            UserUtil.getUserId(principal),
            challengeId,
            feedId,
            commentId,
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 피드 댓글 삭제가 완료되었습니다."))
    }

    @GetMapping("/{challengeId}/feeds/{feedId}")
    @Operation(summary = "챌린지 피드 개별 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND])
    fun findChallengeFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<FindChallengeFeedResponse> {
        val challengeFeed = challengeService.findChallengeFeed(
            UserUtil.getUserId(principal),
            challengeId,
            feedId
        )
        val response = FindChallengeFeedResponse.of(challengeFeed)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/feeds/{feedId}/comments")
    @Operation(
        summary = "챌린지 피드 댓글 리스트 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "댓글 작성 최신순으로 정렬되어 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED])
    fun findChallengeFeedComments(
        principal: Principal,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengeFeedCommentsResponse>> {
        val feedComments = challengeService.findChallengeFeedComments(feedId, page, size)
        val response = SliceResponse.of(feedComments) { FindChallengeFeedCommentsResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/invitation-code")
    @Operation(summary = "챌린지 초대코드 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND])
    fun findChallengeInvitationCode(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeInvitationCodeResponse> {
        val invitationCode = challengeService.findChallengeInvitationCode(
            UserUtil.getUserId(principal),
            challengeId,
        )
        val response = FindChallengeInvitationCodeResponse.of(invitationCode)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/hashtags")
    @Operation(summary = "해시태그 리스트 조회", description = "챌린지에 가장 많이 사용된 순으로 정렬되어 최대 10개 조회됩니다.")
    @ApiResponse(responseCode = "200")
    fun findPopularChallengeHashtags(): ResponseEntity<List<FindPopularChallengeHashtagsResponse>> {
        val hashtags = challengeService.findPopularChallengeHashtags()
        val response = FindPopularChallengeHashtagsResponse.of(hashtags)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-hashtags")
    @Operation(
        summary = "해시태그 모아보기 조회",
        description = "해시태그 파라미터를 지정하면 해당 해시태그가 있는 전체 챌린지 리스트가 조회되고, 파라미터를 '전체'로 지정하면 '전체'로 조회됩니다. 파티원 수가 가장 많은 순으로 정렬됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun findChallengesByHashtag(
        @Parameter(description = "해시태그", example = "러닝") @RequestParam hashtag: String,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengesResponse>> {
        val challenges = challengeService.findChallengesByHashtag(hashtag, page, size)
        val response = SliceResponse.of(challenges) { FindChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search/name")
    @Operation(
        summary = "챌린지 이름 검색",
        description = "[1순위 - 검색어와 완전히 일치하는 이름 / 2순위 - 검색어가 포함된 이름 / 3순위 - 종료 날짜 최신순]으로 현재 진행 중인 챌린지만 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun searchChallengeByName(
        @Parameter(description = "챌린지 이름", example = "러닝 챌린지") @RequestParam challengeName: String,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<SearchChallengeByNameResponse>> {
        val challenges = challengeService.searchChallengeByName(challengeName, page, size)
        val response = SliceResponse.of(challenges) { SearchChallengeByNameResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search/hashtag")
    @Operation(
        summary = "챌린지 해시태그 검색",
        description = "[1순위 - 검색어와 완전히 일치하는 해시태그 / 2순위 - 검색어가 포함된 해시태그 / 3순위 - 종료 날짜 최신순]으로 현재 진행 중인 챌린지만 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun searchChallengeByHashtag(
        @Parameter(description = "챌린지 해시태그", example = "러닝") @RequestParam hashtag: String,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<SearchChallengeByHashtagResponse>> {
        val challenges = challengeService.searchChallengeByHashtag(hashtag, page, size)
        val response = SliceResponse.of(challenges) { SearchChallengeByHashtagResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{challengeId}/join")
    @Operation(
        summary = "챌린지 참여하기",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "건너뛰기 시 { \"goal\": \"\" } 넣어주시면 됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.EXISTING_CHALLENGE_MEMBER, ExceptionCode.CHALLENGE_LIMIT_EXCEED])
    fun joinChallenge(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: JoinChallengeRequest,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.joinChallenge(
            UserUtil.getUserId(principal),
            challengeId,
            request.toServiceDto(),
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 참여하기가 완료되었습니다."))
    }

    @GetMapping("/{challengeId}/invitation-code-match")
    @Operation(summary = "챌린지 초대코드 일치 여부 조회")
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.CHALLENGE_NOT_FOUND])
    fun findChallengeInvitationCodeIsMatch(
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestParam @NotBlank(message = "초대코드는 필수 입력입니다.")
        @Parameter(description = "초대코드", example = "478DS")
        invitationCode: String,
    ): ResponseEntity<FindChallengeInvitationCodeIsMatchResponse> {
        val isMatch = challengeService.isMatchInvitationCode(challengeId, invitationCode)
        val response = FindChallengeInvitationCodeIsMatchResponse.of(isMatch)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{challengeId}/feeds/{feedId}/like")
    @Operation(summary = "챌린지 피드 좋아요", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND, ExceptionCode.EXISTING_FEED_LIKE])
    fun createChallengeFeedLike(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.createChallengeFeedLike(
            UserUtil.getUserId(principal),
            challengeId,
            feedId
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 피드 좋아요가 완료되었습니다."))
    }

    @DeleteMapping("/{challengeId}/feeds/{feedId}/like")
    @Operation(summary = "챌린지 피드 좋아요 취소", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND, ExceptionCode.FEED_LIKE_NOT_FOUND])
    fun deleteChallengeFeedLike(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @PathVariable @Parameter(description = "피드 id", example = "1") feedId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.deleteChallengeFeedLike(
            UserUtil.getUserId(principal),
            challengeId,
            feedId
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 피드 좋아요가 취소되었습니다."))
    }

    @GetMapping("/{challengeId}/feed-existence")
    @Operation(
        summary = "챌린지 인증 피드 존재 여부 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.CHALLENGE_NOT_FOUND])
    fun findChallengeHasFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeHasFeedResponse> {
        val hasFeed = challengeService.hasFeedByChallengeId(challengeId)
        val response = FindChallengeHasFeedResponse.of(hasFeed)
        return ResponseEntity.ok(response)
    }
}
