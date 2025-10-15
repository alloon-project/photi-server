package com.photi.apis.enduser.controller.challenge

import com.photi.apis.enduser.common.exception.annotation.*
import com.photi.apis.enduser.common.success.dto.CollectionSuccessResponse
import com.photi.apis.enduser.common.success.dto.SliceResponse
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.challenge.dto.request.CreateChallengeRequest
import com.photi.apis.enduser.controller.challenge.dto.request.JoinChallengeRequest
import com.photi.apis.enduser.controller.challenge.dto.request.UpdateChallengeRequest
import com.photi.apis.enduser.controller.challenge.dto.response.*
import com.photi.apis.enduser.controller.feed.dto.request.FindImagePreSignedUrlRequest
import com.photi.apis.enduser.controller.feed.dto.response.FindImagePreSignedUrlResponse
import com.photi.core.domain.challenge.exception.ChallengeErrorCode.CHALLENGE_NOT_FOUND
import com.photi.core.domain.challenge.service.ChallengeService
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.*
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
import com.photi.core.domain.userchallengehistory.exception.UserChallengeHistoryErrorCode.CHALLENGE_LIMIT_EXCEED
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
import java.security.Principal

@Validated
@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
class ChallengeController(
    private val challengeService: ChallengeService,
) {

    @PostMapping("/image/pre-signed-url")
    @Operation(summary = "이미지 PresignedURL 조회")
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([FILE_SIZE_EXCEED, IMAGE_TYPE_UNSUPPORTED])
    fun findImagePreSignedUrl(
        @RequestBody @Valid request: FindImagePreSignedUrlRequest,
    ): ResponseEntity<FindImagePreSignedUrlResponse> {
        val preSignedUrl = challengeService.findImagePreSignedUrl(request.toServiceDto())
        val response = FindImagePreSignedUrlResponse.of(preSignedUrl)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    @Operation(summary = "챌린지 개최", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @GlobalApiErrorResponses([EMPTY_FILE_INVALID, TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @UserChallengeHistoryApiErrorResponses([CHALLENGE_LIMIT_EXCEED])
    fun createChallenge(
        principal: Principal,
        @RequestPart @Valid request: CreateChallengeRequest,
    ): ResponseEntity<CreateChallengeResponse> {
        val challenge =
            challengeService.createChallenge(UserUtil.getUserId(principal), request.toServiceDto())
        val response = CreateChallengeResponse.of(challenge)
        return ResponseEntity.status(CREATED).body(response)
    }

    @GetMapping("/example-images")
    @Operation(summary = "챌린지 예시 이미지 리스트 조회")
    @ApiResponse(responseCode = "200")
    fun findChallengeExampleImages(): ResponseEntity<CollectionSuccessResponse> {
        val response = challengeService.findChallengeExampleImages()
        return ResponseEntity.ok(CollectionSuccessResponse(response))
    }

    @GetMapping("/popular")
    @Operation(
        summary = "지금 인기있는 챌린지 조회",
        description = "공개, 비공개 및 종료되지 않은 챌린지가 방문순으로 최대 5개 조회됩니다. 챌린지 파티원 이미지는 최근 가입순으로 최대 3개 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun findPopularChallenges(): ResponseEntity<List<FindPopularChallengesResponse>> {
        val challenges = challengeService.findPopularChallenges()
        val response = FindPopularChallengesResponse.of(challenges)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/info")
    @Operation(summary = "챌린지 소개 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    fun findChallengeIntro(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeIntroResponse> {
        val intro = challengeService.findChallengeIntro(challengeId)
        val response = FindChallengeIntroResponse.of(intro)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    @Operation(summary = "모든 챌린지 조회", description = "종료 날짜 최신순으로 모든 챌린지가 조회됩니다.")
    @ApiResponse(responseCode = "200")
    fun findChallenges(
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<FindChallengesResponse>> {
        val challenges = challengeService.findChallenges(page, size)
        val response = SliceResponse.of(challenges) { FindChallengesResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}")
    @Operation(summary = "챌린지 개별 조회", description = "챌린지 파티원 이미지는 최근 가입순으로 최대 3개 조회됩니다.")
    @ApiResponse(responseCode = "200")
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND, CHALLENGE_CREATOR_FORBIDDEN])
    fun updateChallenge(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestPart @Valid request: UpdateChallengeRequest,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.updateChallenge(
            UserUtil.getUserId(principal),
            challengeId,
            request.toServiceDto(),
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 수정이 완료되었습니다."))
    }

    @DeleteMapping("/{challengeId}")
    @Operation(summary = "챌린지 탈퇴", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    fun withdrawChallenge(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.withdrawChallenge(UserUtil.getUserId(principal), challengeId)
        return ResponseEntity.ok(StringSuccessResponse("챌린지 탈퇴가 완료되었습니다."))
    }

    @GetMapping("/{challengeId}/invitation-code")
    @Operation(summary = "챌린지 초대코드 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
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
    fun searchChallengesByName(
        @Parameter(description = "챌린지 이름", example = "러닝 챌린지") @RequestParam challengeName: String,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<SearchChallengesByNameResponse>> {
        val challenges = challengeService.searchChallengesByName(challengeName, page, size)
        val response = SliceResponse.of(challenges) { SearchChallengesByNameResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search/hashtag")
    @Operation(
        summary = "챌린지 해시태그 검색",
        description = "[1순위 - 검색어와 완전히 일치하는 해시태그 / 2순위 - 검색어가 포함된 해시태그 / 3순위 - 종료 날짜 최신순]으로 현재 진행 중인 챌린지만 조회됩니다.",
    )
    @ApiResponse(responseCode = "200")
    fun searchChallengesByHashtag(
        @Parameter(description = "챌린지 해시태그", example = "러닝") @RequestParam hashtag: String,
        @Parameter(description = "페이지 시작 번호") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "한 페이지당 content 최대 갯수") @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<SliceResponse<SearchChallengesByHashtagResponse>> {
        val challenges = challengeService.searchChallengesByHashtag(hashtag, page, size)
        val response = SliceResponse.of(challenges) { SearchChallengesByHashtagResponse.of(it) }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{challengeId}/join")
    @Operation(
        summary = "챌린지 참여하기",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
        description = "건너뛰기 시 { \"goal\": \"\" } 넣어주시면 됩니다.",
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND, EXISTING_CHALLENGE_MEMBER])
    @UserChallengeHistoryApiErrorResponses([CHALLENGE_LIMIT_EXCEED])
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
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    fun findChallengeInvitationCodeMatches(
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestParam @NotBlank(message = "초대코드는 필수 입력입니다.")
        @Parameter(description = "초대코드", example = "478DS")
        invitationCode: String,
    ): ResponseEntity<FindChallengeInvitationCodeMatchesResponse> {
        val match = challengeService.findChallengeInvitationCodeMatches(challengeId, invitationCode)
        val response = FindChallengeInvitationCodeMatchesResponse.of(match)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{challengeId}/feed-existence")
    @Operation(
        summary = "챌린지 인증 피드 존재 여부 조회",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    fun findChallengeHasFeed(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<FindChallengeHasFeedResponse> {
        val hasFeed = challengeService.findChallengeHasFeed(challengeId)
        val response = FindChallengeHasFeedResponse.of(hasFeed)
        return ResponseEntity.ok(response)
    }
}
