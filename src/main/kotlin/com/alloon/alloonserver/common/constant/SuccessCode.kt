package com.alloon.alloonserver.common.constant

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK

enum class SuccessCode(
    val httpStatus: HttpStatus,
    val message: String,
) {
    /**
     * Report Controller
     */
    // 200 Ok
    FOUND_REPORT_CATEGORIES(OK, "신고 항목을 전체 조회했습니다."),

    // 201 Created
    REPORT_CREATED(CREATED, "신고가 완료 되었습니다."),

    /**
     * Challenge Controller
     */
    // 200 Ok
    FOUND_CHALLENGE_TEMPLATE_IMAGES(OK, "챌린지 예시 이미지를 전체 조회했습니다."),
    CHALLENGE_IMAGE_UPLOADED(OK, "챌린지 이미지를 업로드했습니다."),
    FOUND_POPULAR_CHALLENGES(OK, "지금 인기있는 챌린지를 전체 조회했습니다."),
    NO_POPULAR_CHALLENGES(OK, "지금 인기있는 챌린지가 없습니다."),
    FOUND_CHALLENGE_INFO(OK, "챌린지 소개를 조회했습니다."),
    CHALLENGE_MEMBER_GOAL_UPDATED(OK, "챌린지 개인목표 작성이 완료되었습니다."),
    FOUND_CHALLENGE_MEMBERS(OK, "챌린지 파티원을 전체 조회했습니다."),

    // 201 Created
    CHALLENGE_CREATED(CREATED, "챌린지 생성이 완료되었습니다."),

    /**
     * User Controller
     */
    // 200 OK
    FOUND_MY_USER_INFO(OK, "내 회원 정보를 조회했습니다."),
    USER_IMAGE_UPLOADED(OK, "회원 이미지를 업로드했습니다."),

    /**
     * Auth Controller
     */
    // 201 Created
    EMAIL_VERIFICATION_CODE_SENT(CREATED, "이메일 인증코드를 보냈습니다."),
    USER_REGISTERED(CREATED, "회원 가입을 완료했습니다."),

    // 200 OK
    EMAIL_VERIFICATION_CODE_VERIFIED(OK, "이메일 인증코드가 확인 되었습니다."),
    USERNAME_AVAILABLE(OK, "사용 가능한 아이디입니다."),
    USERNAME_SENT(OK, "아이디를 이메일로 전송했습니다."),
    PASSWORD_SENT(OK, "임시 비밀번호를 이메일로 전송했습니다."),
    USER_LOGIN(OK, "로그인을 했습니다."),
    PASSWORD_CHANGED(OK, "비밀번호가 변경되었습니다."),
    TOKEN_REFRESHED(OK, "토큰이 재발급 됐습니다."),

    /**
     * Develop Controller
     */
    // 200 OK
    SERVER_OK(OK, "헬스 체크를 했습니다."),
    FORCE_UPDATE(OK, "앱 강제 업데이트 여부를 조회했습니다."),
}