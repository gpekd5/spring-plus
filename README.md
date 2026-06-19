# 📝 TodoTalk

## 👨‍🏫 프로젝트 소개

TodoTalk은 TODO 관리 기능과 익명 실시간 채팅 기능을 함께 제공하는 Spring Boot 기반 웹 애플리케이션입니다.

사용자는 회원가입과 로그인을 통해 TODO를 등록하고, 상세 내용을 확인하며 댓글과 담당자를 관리할 수 있습니다.
또한 로그인 여부와 관계없이 익명 닉네임으로 채팅방을 생성하고, WebSocket 기반 실시간 메시지를 주고받을 수 있습니다.

이번 프로젝트에서는 기존 JWT 인증 기반 TODO 서비스에 Spring Security 인증 구조를 적용하고, WebSocket/STOMP 기반 익명 채팅 기능을 추가했습니다.

---

## ⏲️ 개발 기간

* 2026.06.17 ~ 2026.06.19

---

## 🧰 기술 스택

| 구분           | 사용 기술                           |
| ------------ | ------------------------------- |
| Language     | Java 17                         |
| Backend      | Spring Boot, Spring Web MVC     |
| Security     | Spring Security, JWT            |
| Realtime     | Spring WebSocket, STOMP, SockJS |
| Data Access  | Spring Data JPA, QueryDSL       |
| Database     | MySQL                           |
| Frontend     | HTML, CSS, JavaScript           |
| Build / Tool | Gradle, Lombok                  |
| IDE / VCS    | IntelliJ IDEA, Git, GitHub      |

---

## ✨ 주요 기능

<details>
<summary>인증 / 인가</summary>

* 회원가입
* 로그인
* JWT 기반 인증
* Spring Security 기반 인증/인가 처리
* URL별 접근 권한 분리
* 인증 실패 / 인가 실패 JSON 응답 처리

</details>

<details>
<summary>TODO</summary>

* TODO 등록
* TODO 목록 조회
* TODO 상세 조회
* TODO 검색
* TODO 댓글 등록 및 조회
* TODO 담당자 추가 및 삭제

</details>

<details>
<summary>익명 채팅</summary>

* 익명 닉네임 설정
* 채팅방 생성
* 채팅방 목록 조회
* 채팅방별 이전 메시지 조회
* WebSocket/STOMP 기반 실시간 메시지 송수신
* 채팅 메시지 DB 저장

</details>

<details>
<summary>통합 테스트 화면</summary>

* 로그인 / 회원가입 모달
* TODO 등록 팝업
* TODO 목록 / 상세 화면
* 댓글 등록 / 조회
* 담당자 설정 팝업
* 익명 채팅방 생성 / 입장
* 실시간 메시지 송수신 확인

</details>

---

## 📌 주요 구현 내용

<details>
<summary>1. JWT 인증 구조를 Spring Security 기반으로 전환</summary>

### 배경

기존에는 직접 구현한 `JwtFilter`에서 JWT를 검증하고, 인증 정보를 `request.setAttribute()`에 저장한 뒤 Controller에서 사용하는 방식이었습니다.

Spring Security 적용 후에는 인증 정보를 `SecurityContextHolder`에 저장하고, Controller에서는 `@AuthenticationPrincipal`로 인증 사용자를 주입받도록 변경했습니다.

| 구분                | 기존 방식             | Spring Security 적용 후                          |
| ----------------- | ----------------- | --------------------------------------------- |
| 인증 처리 위치          | 직접 구현한 Filter     | Spring Security FilterChain                   |
| 사용자 정보 저장         | request attribute | SecurityContextHolder                         |
| Controller 사용자 주입 | 커스텀 `@Auth`       | `@AuthenticationPrincipal`                    |
| 권한 확인             | Filter에서 직접 처리    | SecurityConfig에서 URL별 권한 처리                   |
| 인증/인가 예외 처리       | Filter에서 직접 응답    | AuthenticationEntryPoint, AccessDeniedHandler |

### 구현 내용

* `SecurityFilterChain` 기반 인증/인가 설정
* JWT 검증 후 `Authentication` 객체 생성
* 인증 정보를 `SecurityContextHolder`에 저장
* 인증 실패 시 `AuthenticationEntryPoint`에서 JSON 응답 반환
* 인가 실패 시 `AccessDeniedHandler`에서 JSON 응답 반환

</details>

<details>
<summary>2. WebSocket/STOMP 기반 익명 채팅 구현</summary>

### 구현 내용

* WebSocket 연결 엔드포인트 `/ws` 추가
* STOMP 발행 prefix `/pub` 설정
* STOMP 구독 prefix `/sub` 설정
* 채팅방 생성 API 추가
* 채팅방 목록 조회 API 추가
* 채팅방별 이전 메시지 조회 API 추가
* 메시지 전송 시 DB 저장 후 구독자에게 브로드캐스트

### WebSocket 경로

| 구분        | 경로                   | 설명           |
| --------- | -------------------- | ------------ |
| Endpoint  | `/ws`                | WebSocket 연결 |
| Publish   | `/pub/chat.send`     | 메시지 전송       |
| Subscribe | `/sub/chat/{roomId}` | 채팅방별 메시지 구독  |

### 메시지 흐름

```text
클라이언트
  ↓ /pub/chat.send
ChatController
  ↓
ChatMessage DB 저장
  ↓
SimpMessagingTemplate
  ↓ /sub/chat/{roomId}
구독 중인 클라이언트에게 실시간 전송
```

### 구현 포인트

현재 채팅은 Redis Pub/Sub 기반이 아니라 Spring 내부 Simple Broker 기반입니다.

단일 서버 환경에서 WebSocket/STOMP 메시지 송수신을 검증하는 구조로 구현했습니다.

</details>

<details>
<summary>3. 통합 테스트 화면 구성</summary>

### 구현 내용

별도의 프론트엔드 프로젝트 없이 주요 API와 WebSocket 기능을 확인할 수 있도록 정적 HTML 페이지를 추가했습니다.

### 화면에서 확인 가능한 기능

* 회원가입
* 로그인
* TODO 등록
* TODO 목록 조회
* TODO 상세 조회
* 댓글 등록 및 조회
* 담당자 추가 및 삭제
* 채팅방 생성
* 채팅방 목록 조회
* 이전 메시지 조회
* 실시간 메시지 송수신

### 접속 경로

```text
http://localhost:8080/chat-test.html
```

</details>

---

## 🧭 API / WebSocket 명세

<details>
<summary>API 명세 보기</summary>

### Auth

| Method | URL            | 설명   | 인증  |
| ------ | -------------- | ---- | --- |
| POST   | `/auth/signup` | 회원가입 | 불필요 |
| POST   | `/auth/signin` | 로그인  | 불필요 |

### TODO

| Method | URL               | 설명         | 인증 |
| ------ | ----------------- | ---------- | -- |
| POST   | `/todos`          | TODO 등록    | 필요 |
| GET    | `/todos`          | TODO 목록 조회 | 필요 |
| GET    | `/todos/{todoId}` | TODO 상세 조회 | 필요 |
| GET    | `/todos/search`   | TODO 검색    | 필요 |

### Comment

| Method | URL                        | 설명    | 인증 |
| ------ | -------------------------- | ----- | -- |
| POST   | `/todos/{todoId}/comments` | 댓글 등록 | 필요 |
| GET    | `/todos/{todoId}/comments` | 댓글 조회 | 필요 |

### Manager

| Method | URL                                    | 설명     | 인증 |
| ------ | -------------------------------------- | ------ | -- |
| POST   | `/todos/{todoId}/managers`             | 담당자 추가 | 필요 |
| GET    | `/todos/{todoId}/managers`             | 담당자 조회 | 필요 |
| DELETE | `/todos/{todoId}/managers/{managerId}` | 담당자 삭제 | 필요 |

### Chat

| Method | URL                                 | 설명        | 인증  |
| ------ | ----------------------------------- | --------- | --- |
| POST   | `/api/chat/rooms`                   | 채팅방 생성    | 불필요 |
| GET    | `/api/chat/rooms`                   | 채팅방 목록 조회 | 불필요 |
| GET    | `/api/chat/rooms/{roomId}/messages` | 이전 메시지 조회 | 불필요 |

### WebSocket

| 구분        | 경로                   | 설명           |
| --------- | -------------------- | ------------ |
| Endpoint  | `/ws`                | WebSocket 연결 |
| Publish   | `/pub/chat.send`     | 메시지 전송       |
| Subscribe | `/sub/chat/{roomId}` | 채팅방 메시지 구독   |

</details>

---

## 🚀 실행 방법

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd <project-name>
```

### 2. DB 설정

`src/main/resources/application.yml`에서 MySQL 접속 정보를 설정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db
    username: your_username
    password: your_password
```

### 3. 서버 실행

```bash
./gradlew bootRun
```

또는 IntelliJ에서 Spring Boot 애플리케이션을 실행합니다.

### 4. 테스트 화면 접속

```text
http://localhost:8080/chat-test.html
```

---

## ✅ 주요 검증 방법

<details>
<summary>TODO 기능 검증</summary>

1. 회원가입
2. 로그인
3. TODO 등록
4. TODO 목록 조회
5. TODO 선택 후 상세 조회
6. 댓글 등록
7. 댓글 조회
8. 담당자 추가
9. 담당자 삭제

</details>

<details>
<summary>채팅 기능 검증</summary>

1. 브라우저에서 `chat-test.html` 접속
2. 익명 닉네임 입력
3. 채팅방 생성
4. 채팅방 입장
5. 메시지 전송
6. 다른 브라우저 창에서 같은 채팅방 입장
7. 실시간 메시지 수신 확인

</details>

---

## 🔥 Trouble Shooting

<details>
<summary>1. WebSocket 연결과 채팅 API 요청이 인증 필터에 막히는 문제</summary>

### 문제

익명 채팅 기능은 로그인 없이 사용할 수 있어야 했지만, Spring Security 설정에 따라 WebSocket 연결과 채팅 API 요청이 인증 필터에서 차단될 수 있었습니다.

### 원인

`/ws/**`, `/api/chat/**` 경로가 인증 필요 대상으로 남아 있으면 JWT가 없는 익명 사용자는 WebSocket 연결 또는 채팅 API 요청을 정상적으로 사용할 수 없습니다.

특히 SockJS를 사용할 경우 `/ws/info` 같은 사전 요청도 발생하므로 `/ws`만이 아니라 `/ws/**` 전체 허용이 필요했습니다.

### 해결

채팅 관련 경로와 정적 테스트 페이지를 인증 제외 대상으로 분리했습니다.

```java
.requestMatchers("/chat-test.html", "/favicon.ico").permitAll()
.requestMatchers("/auth/**").permitAll()
.requestMatchers("/ws/**").permitAll()
.requestMatchers("/api/chat/**").permitAll()
.requestMatchers("/error").permitAll()
.anyRequest().authenticated()
```

### 결과

* 익명 사용자의 WebSocket 연결 가능
* 익명 사용자의 채팅방 생성 / 조회 가능
* TODO 기능은 기존처럼 로그인 사용자만 접근 가능

</details>

<details>
<summary>2. 내부 예외가 Security 응답에 가려진 문제</summary>

### 문제

QueryDSL 검색 API 내부에서 예외가 발생했지만, 실제 QueryDSL 오류가 바로 보이지 않고 Security 인증/인가 예외 응답처럼 보였습니다.

### 원인

내부 예외 발생 후 Spring Boot가 `/error` 경로로 이동했는데, `/error` 경로도 Security 보호 대상에 포함되어 있었습니다.

그 결과 실제 내부 예외가 아니라 Security 예외 응답이 먼저 반환되어 원인 파악이 어려웠습니다.

### 해결

`/error` 경로를 인증 제외 대상으로 추가했습니다.

```java
.requestMatchers("/error").permitAll()
```

### 자세한 정리

* [Spring Security 적용 후 실제 예외가 Security 응답에 가려진 문제 정리](https://velog.io/@gpekd5/TIL-TroubleShooting-Spring-Security-%EC%A0%81%EC%9A%A9-%ED%9B%84-%EC%8B%A4%EC%A0%9C-%EC%98%88%EC%99%B8-%EC%9B%90%EC%9D%B8-%EC%B0%BE%EA%B8%B0-%EC%96%B4%EB%A0%A4%EC%9B%A0%EB%8D%98-%EC%9D%B4%EC%9C%A0)

</details>

---

## 📚 배운 점

<details>
<summary>실시간 응답에서는 왜 WebSocket을 사용할까?</summary>

HTTP는 요청과 응답이 명확한 일반적인 API에 적합합니다.
하지만 채팅처럼 서버의 변경 사항을 즉시 클라이언트 화면에 반영해야 하는 기능에서는 반복 요청이 필요한 Polling 방식의 한계가 있습니다.

WebSocket은 클라이언트와 서버가 한 번 연결을 맺은 뒤 그 연결을 유지하면서 양방향으로 데이터를 주고받을 수 있습니다.

이번 익명 채팅 기능에서는 사용자가 메시지를 보내면 서버가 같은 채팅방을 구독 중인 사용자들에게 즉시 메시지를 전달하도록 구현했습니다.

### 자세한 정리

* [실시간 응답에서는 왜 WebSocket을 사용할까?](https://velog.io/@gpekd5/%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9D%91%EB%8B%B5%EC%97%90%EC%84%9C%EB%8A%94-%EC%99%9C-WebSocket%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%A0%EA%B9%8C)

</details>

---

## ⚠️ 한계 및 개선 예정

<details>
<summary>개선 예정 사항</summary>

이번 구현은 단일 서버 환경에서 동작하는 Spring Simple Broker 기반 채팅입니다.

추후 개선할 수 있는 내용은 다음과 같습니다.

* Redis Pub/Sub 기반 분산 채팅 구조 적용
* 입력 중 표시 기능
* 채팅방 입장/퇴장 알림
* 온라인 사용자 목록
* 메시지 읽음 처리
* 채팅방 권한 관리
* 프론트엔드 프로젝트 분리
* WebSocket 테스트 코드 추가

</details>

---

## 🤖 AI 사용 내역

| 항목            | 사용 도구   | AI 제안 내용                                           | 실제 적용 내용                                          |
| ------------- | ------- | -------------------------------------------------- | ------------------------------------------------- |
| HTML 화면 구성 보조 | ChatGPT | TODO, 댓글, 담당자, 채팅 기능을 한 화면에서 확인할 수 있는 정적 페이지 구성 제안 | 프로젝트 API 경로와 DTO 구조에 맞게 수정하여 `chat-test.html`에 반영 |
| UI 흐름 정리      | ChatGPT | 로그인/회원가입 모달, TODO 등록 팝업, 담당자 설정 팝업 구조 제안           | 실제 시연 흐름에 맞게 필요한 UI만 선별 적용                        |
| 프론트 요청 흐름 점검  | ChatGPT | WebSocket 연결, 채팅방 구독, 댓글 등록 요청 흐름 확인 방법 제안         | 브라우저에서 직접 동작 확인 후 필요한 부분만 반영                      |

---

## ✅ 회고

이번 프로젝트를 통해 기존 HTTP API 중심의 애플리케이션에 WebSocket 기반 실시간 기능을 추가하는 흐름을 경험했습니다.

REST API는 요청과 응답이 명확한 반면, WebSocket은 연결, 구독, 발행, 브로드캐스트 흐름을 함께 고려해야 했습니다.

또한 로그인 기반 TODO 기능과 익명 채팅 기능이 같은 애플리케이션 안에 존재하면서, 기능별 인증 정책을 분리하는 것이 중요하다는 점을 알게 되었습니다.

추후에는 Redis Pub/Sub을 적용하여 다중 서버 환경에서도 메시지가 안정적으로 전달되는 구조로 개선해보고 싶습니다.
