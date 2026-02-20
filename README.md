<img width="100%" alt="Image" src="https://github.com/user-attachments/assets/c08b3bb3-979c-44ba-9c40-94ad1309a8f1" />

<hr style="width:50%;margin:auto;"/>

# 포티&nbsp;&nbsp;[![iOS](https://img.shields.io/badge/iOS-Download-0D96F6?style=flat&logo=appstore&logoColor=white)](https://apps.apple.com/kr/app/%ED%8F%AC%ED%8B%B0/id6747941953) [![Android](https://img.shields.io/badge/Android-Download-0F9D58?style=flat&logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.photi.aos&pcampaignid=web_share) <img src="https://github.com/user-attachments/assets/1406ec1d-f4d6-44fd-ba6a-3c221588c4b2" align=left width=100>

> 포토 챌린지로 일상 속 새로운 즐거움을 발견해 보세요! • <b>백엔드</b> 레포지토리
<br/>

<br/>

<div align="center">
<img width="24%" alt="Image" src="https://github.com/user-attachments/assets/ce8bfc1a-18c8-4d60-b4cf-7166fcfe14fe">
<img width="24%" alt="Image" src="https://github.com/user-attachments/assets/460641b8-e967-4dc1-8e0d-9f464070eb9b" />
<img width="24%" alt="Image" src="https://github.com/user-attachments/assets/3e03d6f6-d163-48cc-a047-37a2d533b204" />
<img width="24%" alt="Image" src="https://github.com/user-attachments/assets/2c7be02b-b378-4c95-ae51-4c7af8083a41" />
</div>

## 💭 Features
1. **1일 1 인증** - 하루 한 번 정해진 시간 안에 즉석에서 챌린지 사진 인증
2. **챌린지 만들기** - 이름, 목표, 이미지, 인증 시간, 인증 룰, 해시태그, 종료 날짜, 공개 여부를 설정하여 챌린지 생성
3. **다양한 챌린지 탐색** - 인기/해시태그/검색으로 관심 있는 챌린지 탐색
4. **챌린지 함께하기** - 비공개 챌린지는 초대 코드를 통해 참여 / 공개 챌린지는 자유롭게 참여
5. **파티원과 함께 도전하기** - 챌린지 인증 사진에 댓글과 좋아요 / 개인 목표 설정
6. **인증 사진 공유하기** - 인스타그램으로 나의 챌린지 인증 공유

## ✨ Service

- [서비스 소개 노션](https://octagonal-caboc-47d.notion.site/team-photi?source=copy_link)
- [1차 MVP 기획](https://www.figma.com/board/hG9r7mpCYJ9lqOLr2YUNKE/%E2%9A%A1%EF%B8%8F-%ED%8C%80-%EA%B8%B0%ED%9A%8D?node-id=255-289&t=G3Gv8kj3VF6SWywH-1)
- [2차 MVP 기획](https://www.figma.com/board/hG9r7mpCYJ9lqOLr2YUNKE/%E2%9A%A1%EF%B8%8F-%ED%8C%80-%EA%B8%B0%ED%9A%8D?node-id=0-1&t=G3Gv8kj3VF6SWywH-1)
- [인스타그램](https://www.instagram.com/photi_official/)

## 📚 Tech Stack

<div>
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Image">
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white" alt="Image">
</div>

<div>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1.svg?style=flat-square&logo=postgresql&logoColor=white" alt="Image">
<img src="https://img.shields.io/badge/Redis-FF4438?style=flat-square&logo=Redis&logoColor=white" alt="Image">
<img src="https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white" alt="Image">
</div>

<div>
<img src="https://img.shields.io/badge/Spring Security-6DB33F.svg?style=flat-square&logo=springsecurity&logoColor=white" alt="Image">
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=flat-square&logo=jsonwebtokens&logoColor=white" alt="Image">
</div>

<div>
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&&logoColor=white" alt="Image">
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white" alt="Image">
</div>

<div>
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black" alt="Image">
</div>

## 📁 Project Structure

```bash
├── photi-apis
│   └── photi-apis:enduser
│       ├── config # email, security, swagger 설정 등
│       └── controller
│           └── <도메인 별 패키지> # ex) user, challenge, feed 등
│               └── dto # 컨트롤러 계층에서 사용하는 dto
│                   ├── request
│                   └── response
├── photi-batch # 배치 애플리케이션
├── photi-core
│   ├── photi-core:domain # 도메인(핵심 비즈니스 로직)
│   │   ├── common # 공통 에러 코드, BaseEntity 등  
│   │   └── <도메인 별 패키지> # ex) user, challenge, feed 등
│   │       ├── adapter # 다른 도메인에서 호출한 port 인터페이스를 구현한 클래스
│   │       ├── dto # 서비스 계층에서 사용하는 dto
│   │       ├── exception # 도메인별 에러 코드, 예외 정의
│   │       ├── model # 도메인 엔티티
│   │       │   └── repository # JPA 레포지토리
│   │       ├── service
│   │       │   ├── 각 도메인 별 service # 컨트롤러 계층에서 사용
│   │       │   ├── command # 명령 서비스 - 명령과 관련된 메소드(create, update, delete) 구현
│   │       │   └── query # 조회 서비스 - 조회와 관련된 메소드(read) 구현
│   │       ├── port # 다른 도메인을 호출하는 port 인터페이스
│   │       └── validator # 유효성 검사
│   └── photi-core:infra # async, jpa, querydsl, redis, s3 설정
└── photi-utils # 유틸성 object 클래스
```

## 💻 Developer

<table>
    <tr align="center">
        <td><B>Backend</B></td>
    </tr>
    <tr align="center">
        <td><B>김유경</B></td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://avatars.githubusercontent.com/u/58517873?v=4" width="100" height="100"/>
            <br>
            <a href="https://github.com/YuGyeong98">YuGyeong98</a>
        </td>
    </tr>
</table>
