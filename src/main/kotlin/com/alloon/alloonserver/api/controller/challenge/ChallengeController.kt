package com.alloon.alloonserver.api.controller.challenge

import com.alloon.alloonserver.api.controller.challenge.request.CreateChallengeRequest
import com.alloon.alloonserver.api.controller.challenge.request.UpdateChallengeMemberGoalRequest
import com.alloon.alloonserver.api.controller.challenge.response.CreateChallengeResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeInfoResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeMembersResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindPopularChallengesResponse
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.*
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.alloon.alloonserver.service.challenge.ChallengeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "챌린지 생성 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다."),
            ApiResponse(
                responseCode = "415",
                description = "이미지는 '.jpeg', '.jpg', '.png', '.gif' 타입만 가능합니다."
            ),
        ]
    )
    fun createChallenge(
        principal: Principal,
        @RequestPart @Valid request: CreateChallengeRequest,
        @RequestPart imageFile: MultipartFile
    ): ResponseEntity<DefaultSingleResponse> {
        val challenge = challengeService.createChallenge(
            UserUtility.getUserId(principal),
            request.toServiceDto(),
            imageFile
        )
        val response = CreateChallengeResponse.of(challenge)

        return DefaultSingleResponse.toResponseEntity(CHALLENGE_CREATED, response)
    }

    @GetMapping("/api/challenges/example-images")
    @Operation(summary = "챌린지 예시 이미지 리스트 조회")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "챌린지 예시 이미지 리스트 조회 성공")])
    fun getChallengeExampleImages(): ResponseEntity<DefaultListResponse<String>> {
        val response = challengeService.getChallengeExampleImages()

        return DefaultListResponse.toResponseEntity(FOUND_CHALLENGE_TEMPLATE_IMAGES, response)
    }

    @GetMapping("/api/challenges/popular")
    @Operation(
        summary = "지금 인기있는 챌린지 조회",
        description = "공개, 비공개 및 종료되지 않은 챌린지가 방문순으로 최대 5개 조회됩니다."
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "지금 인기있는 챌린지 조회 성공")])
    fun findPopularChallenges(): ResponseEntity<DefaultListResponse<FindPopularChallengesResponse>> {
        val response = FindPopularChallengesResponse.of(challengeService.findPopularChallenges())
        val successCode =
            if (response.isNotEmpty()) FOUND_POPULAR_CHALLENGES else NO_POPULAR_CHALLENGES

        return DefaultListResponse.toResponseEntity(successCode, response)
    }

    @GetMapping("/api/challenges/{challengeId}/info")
    @Operation(summary = "챌린지 소개 조회", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "챌린지 소개 조회 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 챌린지입니다."),
        ]
    )
    fun findChallengeInfo(
        principal: Principal,
        @PathVariable challengeId: Long
    ): ResponseEntity<DefaultSingleResponse> {
        val challengeInfo = challengeService.findChallengeInfo(challengeId)
        val response = FindChallengeInfoResponse.of(challengeInfo)
        return DefaultSingleResponse.toResponseEntity(FOUND_CHALLENGE_INFO, response)
    }

    @PatchMapping("/api/challenges/{challengeId}/challenge-members/goal")
    @Operation(summary = "챌린지 개인목표 작성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "챌린지 개인목표 작성 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(responseCode = "404", description = "존재하지 않는 챌린지 파티원입니다."),
        ]
    )
    fun updateChallengeMemberGoal(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: UpdateChallengeMemberGoalRequest,
    ): ResponseEntity<DefaultResponse> {
        challengeService.updateChallengeMemberGoal(
            UserUtility.getUserId(principal),
            challengeId,
            request.toServiceDto()
        )

        return DefaultResponse.toResponseEntity(CHALLENGE_MEMBER_GOAL_UPDATED)
    }

    @GetMapping("/api/challenges/{challengeId}/challenge-members")
    @Operation(
        summary = "챌린지 파티원 조회",
        description = "파티장 -> 본인 -> 가입순으로 파티원이 전체 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)]
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "챌린지 파티원 조회 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
    fun findChallengeMembers(
        principal: Principal,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<DefaultPageResponse<FindChallengeMembersResponse>> {
        val challengeMembers =
            challengeService.findChallengeMembers(UserUtility.getUserId(principal), challengeId)
        val response = FindChallengeMembersResponse.of(challengeMembers)
        return DefaultPageResponse.toResponseEntity(
            FOUND_CHALLENGE_MEMBERS,
            PageData(response, response.size.toLong())
        )
    }
}