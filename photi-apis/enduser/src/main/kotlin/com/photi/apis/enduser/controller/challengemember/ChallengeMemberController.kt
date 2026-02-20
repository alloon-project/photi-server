package com.photi.apis.enduser.controller.challengemember

import com.photi.apis.enduser.common.exception.ChallengeMemberApiErrorResponses
import com.photi.apis.enduser.common.exception.GlobalApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.config.security.AuthUser
import com.photi.apis.enduser.config.security.CustomUserDetails
import com.photi.apis.enduser.config.security.getUserId
import com.photi.apis.enduser.controller.challengemember.dto.request.RegisterChallengePersonalGoalRequest
import com.photi.apis.enduser.controller.challengemember.dto.response.FindChallengeMembersResponse
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.challengemember.service.ChallengeMemberService
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v2/challenge-members")
@Tag(name = "ChallengeMember", description = "챌린지 파티원 API")
class ChallengeMemberController(
    private val challengeMemberService: ChallengeMemberService,
) {

    @PatchMapping("/{challengeId}/goal")
    @Operation(summary = "챌린지 개인목표 작성", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    fun registerChallengePersonalGoal(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
        @RequestBody @Valid request: RegisterChallengePersonalGoalRequest,
    ): ResponseEntity<StringSuccessResponse> {
        challengeMemberService.registerChallengePersonalGoal(
            user.getUserId(),
            challengeId,
            request.toServiceDto(),
        )
        return ResponseEntity.ok(StringSuccessResponse("챌린지 개인목표 작성이 완료되었습니다."))
    }

    @GetMapping("/{challengeId}")
    @Operation(
        summary = "챌린지 파티원 조회",
        description = "파티장 -> 본인 -> 가입순으로 파티원이 전체 조회됩니다.",
        security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)],
    )
    @ApiResponse(responseCode = "200")
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, EXPIRED_TOKEN, INVALID_TOKEN])
    fun findChallengeMembers(
        @AuthUser user: CustomUserDetails,
        @PathVariable @Parameter(description = "챌린지 id", example = "1") challengeId: Long,
    ): ResponseEntity<List<FindChallengeMembersResponse>> {
        val challengeMembers =
            challengeMemberService.findChallengeMembers(user.getUserId(), challengeId)
        val response = FindChallengeMembersResponse.of(challengeMembers)
        return ResponseEntity.ok(response)
    }
}
