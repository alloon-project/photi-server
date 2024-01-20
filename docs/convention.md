# Convention

## Table of Contents 📋

1. [Naming & Code](#naming--code)
2. [Test](#test)
3. [Git Message](#git-message)
4. [Branch](#branch)
5. [Etc](#etc)

## 1️⃣ Naming & Code<a id="naming--code"></a>

### Service

- 검증 - `validateXXX()`
- 단건 조회 - `findXXX()`
- 다건 조회 - `findPageXXX()`
- 메소드명과 발생할 수 있는 모든 예외 케이스 주석으로 작성

```kotlin
@Service
@Transactional(readOnly = true)
class Service(
	private val repository: Repository,
) {

	/**
	 * 메소드명 |
	 * 400(ERROR_ONE)
	 * 409(ERROR_TWO)
     * @param request 매개변수명
     * @return 반환객체명
	 */
	fun doSomething(request: Request): Response {
		if (case1)
			throw CustomException(ERROR_ONE)
		if (case2)
			throw CustomException(ERROR_TWO)
		// ...
	}
}
```

### Repository

- 단건 조회 - `findXXX()`
- 다건 조회 - `findPageXXX()`

### Domain

- setter 사용 금지
- 값을 가져오는 메소드 - `val`
- 상태를 변경하는 메소드 - `fun`
-  메소드명과 발생할 수 있는 모든 예외 케이스 주석으로 작성

```kotlin
class Domain(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "domain_id")
	val id: Long? = null,
) {

	/**
	 * 메소드명 |
     * 400(ERROR_ONE)
     * 409(ERROR_TWO)
     * @param object 매개변수명
	 */
	fun doSomething(object: Object) {
		if (case1)
			throw CustomException(ERROR_ONE)
		if (case2)
			throw CustomException(ERROR_TWO)
		// ...
	}
}
```

## 2️⃣ Test<a id="test"></a>

정상 케이스 아래에 예외 케이스 작성

#### Controller

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

##### Example

- 정상
    - `givenValid_whenRegister_thenReturn200` - `회원 가입을 하면 201을 반환한다`
- 예외
    - `givenBlankUsername_whenRegister_thenThrow400` - `아이디 없이 회원 가입을 하면 400을 던진다`
    - `givenBlankPassword_whenRegister_thenThrow400` - `비밀번호 없이 회원 가입을 하면 400을 던진다`

```kotlin
@ActiveProfile("test")
@WebMvcTest(Controller::class)
@AutoConfigureMockMvc(addFilters = false)
class ControllerTest(
	@Autowired private val mockMvc: MockMvc,
	@Autowired private val objectMapper: ObjectMapper,
	@MockBean private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
	@MockBean private val service: Service,
) {

	@Test
	@DisplayName("XXX을 하면 2XX를 반환한다")
	fun givenValid_whenXXX_thenReturn2XX() { }

	@Test
	@DisplayName("XXX로 XXX을 하면 4XX을 반환한다")
	fun givenXXX_whenXXX_thenThrow4XX() { }
}
```

#### Service

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

##### Example

- 정상
    - `givenValid_whenRegistger_thenRetrun` - `회원 가입을 하면 정상 작동한다`
- 예외
    - `givenExistingUsername_whenRegister_thenThrow` - `존재하는 회원 아이디로 회원 가입을 하면 예외가 발생한다`
    - `givenUnequalPasswordAndPaswordReEntered_whenRegister_thenThrow` - `동일하지 않은 비밀번호와 비밀번호 재입력으로 회원 가입을 하면 예외가 발생한다`

```kotlin
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceTest(
	@Autowired private val service: Service,
	@Autowired private val repository: Repository,
) {

	 @Test
	 @DisplayName("XXX을 하면 정상 작동한다")
	 fun givenValid_whenXXX_thenReturn() { }

	 @Test
	 @DisplayName("XXX로 XXX을 하면 예외가 발생한다")
	 fun givenXXX_whenXXX_thenThrow() { }
}
```

#### Repository

- 모든 정상 케이스 작성
- 데이터베이스와 관련된 예외 케이스만 작성

##### Example

- 정상
    - `givenValid_whenSave_thenReturn` - `회원을 저장하면 정상 작동한다`
- 예외
    - `givenExistingUsername_whenSave_thenThrow` - `존재하는 회원 아이디로 회원 저장을 하면 예외가 발생한다`

```kotlin
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RepositoryTest(
	@Autowired private val repository: Repository,
) {

	@Test
	@DisplayName("XXX을 하면 정상 작동한다")
	fun givenValid_whenXXX_thenReturn() { }

	@Test
	@DisplayName("XXX로 XXX을 하면 예외가 발생한다")
	fun givenXXX_whenXXX_thenThrowXXX() { }

}
```

#### Domain

- 모든 정상 케이스 작성
- 모든 예외 케이스 작성

```java
class DomainTest {

}
```

## 3️⃣ Git Message<a id="git-message"></a>

### Commit Type

- FEAT: 새로운 기능 추가
- FIX: 버그 수정
- DOCS: 문서 수정
- STYLE: 코드 포맷팅 또는 코드 변경이 없는 경우
- REFACTOR: 코드 리펙토링
- TEST: 테스트 코드, 리펙토링 테스트 코드 추가
- CHORE: 빌드 업무 수정, 패키지 매니저 수정

### Subject

- 너무 길게 작성하지 않는다
- 맨 뒤에 Issue 번호를 함께 작성한다

##### Example

```
git commit -m "[FEAT] 새로운 기능 추가 #1"
```

## 4️⃣ Branch<a id="branch"></a>

### Strategy

[Git-flow](https://techblog.woowahan.com/2553/)

##### Example

- master
- develop
- feat/1
- feat/2

## 5️⃣ Etc<a id="etc"></a>

- Intellij 상에 수직선(visual guide) 내로 코드 작성