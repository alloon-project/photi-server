package com.alloon.alloonserver.common.constant

enum class EmailConstants(
    val subject: String,
    val content: String,
) {
    REGISTER_VERIFICATION_CODE("[포티] 가입 인증코드", "안녕하세요! 가입 절차를 계속하기 위해 아래의 코드를 이메일 인증코드란에 입력해 주세요."),
    FORGOT_USERNAME("[포티] 아이디 찾기", "안녕하세요! 회원님의 아이디 입니다."),
    FORGOT_PASSWORD("[포티] 비밀번호 찾기", "안녕하세요! 임시 비밀번호 입니다.")
}