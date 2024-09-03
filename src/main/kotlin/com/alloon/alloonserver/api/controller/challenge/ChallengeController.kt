package com.alloon.alloonserver.api.controller.challenge

import com.alloon.alloonserver.api.controller.challenge.request.CreateChallengeRequest
import com.alloon.alloonserver.api.controller.challenge.request.UpdateChallengeMemberGoalRequest
import com.alloon.alloonserver.api.controller.challenge.response.CreateChallengeResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeInfoResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeMembersResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindPopularChallengesResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.ApiErrorResponses
import com.alloon.alloonserver.common.response.CollectionSuccessResponse
import com.alloon.alloonserver.common.response.StringSuccessResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.alloon.alloonserver.service.challenge.ChallengeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Validated
@RestController
@Tag(name = "Challenge", description = "챌린지 API")
class ChallengeController(
    private val challengeService: ChallengeService
) {

    @PostMapping("/api/challenges", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "챌린지 생성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([EMPTY_FILE_INVALID, TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND, IMAGE_TYPE_UNSUPPORTED])
    fun createChallenge(
        principal: Principal,
        @RequestPart @Valid request: CreateChallengeRequest,
        @RequestPart imageFile: MultipartFile
    ): ResponseEntity<CreateChallengeResponse> {
        val challenge = challengeService.createChallenge(
            UserUtility.getUserId(principal),
            request.toServiceDto(),
            imageFile
        )
        val response = CreateChallengeResponse.of(challenge)

        return ResponseEntity.status(CREATED).body(response)
    }

    @GetMapping("/api/challenges/example-images")
    @Operation(summary = "챌린지 예시 이미지 리스트 조회")
    @ApiResponse(responseCode = "200")
    fun getChallengeExampleImages(): ResponseEntity<CollectionSuccessResponse> {
        val response = challengeService.getChallengeExampleImages()

        return ResponseEntity.ok(CollectionSuccessResponse(response))
    }

    @GetMapping("/api/challenges/popular")
    @Operation(
        summary = "지금 인기있는 챌린지 조회",
        description = "공개, 비공개 및 종료되지 않은 챌린지가 방문순으로 최대 5개 조회됩니다."
    )
    @ApiResponse(responseCode = "200")
    fun findPopularChallenges(): ResponseEntity<List<FindPopularChallengesResponse>> {
        val response = FindPopularChallengesResponse.of(challengeService.findPopularChallenges())

        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/challenges/{challengeId}/info")
    @Operation(summary = "챌린지 소개 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, CHALLENGE_NOT_FOUND])
    fun findChallengeInfo(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long
    ): ResponseEntity<FindChallengeInfoResponse> {
        val challengeInfo = challengeService.findChallengeInfo(challengeId)
        val response = FindChallengeInfoResponse.of(challengeInfo)

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/api/challenges/{challengeId}/challenge-members/goal")
    @Operation(summary = "챌린지 개인목표 작성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, CHALLENGE_MEMBER_NOT_FOUND])
    fun updateChallengeMemberGoal(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: UpdateChallengeMemberGoalRequest,
    ): ResponseEntity<StringSuccessResponse> {
        challengeService.updateChallengeMemberGoal(
            UserUtility.getUserId(principal),
            challengeId,
            request.toServiceDto()
        )

        return ResponseEntity.ok(StringSuccessResponse("챌린지 개인목표 작성이 완료되었습니다."))
    }

    @GetMapping("/api/challenges/{challengeId}/challenge-members")
    @Operation(
        summary = "챌린지 파티원 조회",
        description = "파티장 -> 본인 -> 가입순으로 파티원이 전체 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponse(responseCode = "200")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    fun findChallengeMembers(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<List<FindChallengeMembersResponse>> {
        val challengeMembers =
            challengeService.findChallengeMembers(UserUtility.getUserId(principal), challengeId)
        val response = FindChallengeMembersResponse.of(challengeMembers)

        return ResponseEntity.ok(response)
    }
}