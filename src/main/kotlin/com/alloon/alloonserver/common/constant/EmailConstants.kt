package com.alloon.alloonserver.common.constant

enum class EmailConstants(
    val subject: String,
    val content: String,
) {
    REGISTER_VERIFICATION_CODE("[얼른] 가입 인증코드", "안녕하세요!🙇🏻<br>가입 절차를 계속하기 위해 아래의 코드를 이메일 인증코드란에 입력해 주세요."),

}