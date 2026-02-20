package com.photi.core.domain.user.port.email

enum class EmailTemplate(
    val subject: String,
    val content: String,
) {
    SIGN_UP_AUTHENTICATION_CODE(
        "[포티] 가입 인증코드",
        "안녕하세요! 가입 절차를 계속하기 위해 아래의 코드를 이메일 인증코드란에 입력해 주세요.",
    ),
    FIND_USERNAME("[포티] 아이디 찾기", "안녕하세요! 회원님의 아이디 입니다."),
    FIND_PASSWORD("[포티] 비밀번호 찾기", "안녕하세요! 임시 비밀번호 입니다."),
}
