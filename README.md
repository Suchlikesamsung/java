# Member Management API

[![CI](https://github.com/Suchlikesamsung/java/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Suchlikesamsung/java/actions/workflows/ci.yml)

Spring Boot로 만든 회원 관리 REST API입니다.

단순히 CRUD만 만드는 것에서 끝내지 않고, 실제 백엔드 프로젝트에서 자주 마주치는 구조를 한 번에 정리해보고 싶어서 만든 프로젝트입니다.  
JPA, QueryDSL, JWT 인증, 공통 응답 포맷, 전역 예외 처리, Swagger 문서화, 테스트 자동화까지 기본기를 작게나마 담아두었습니다.

## Tech Stack

- Java 21
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- QueryDSL
- H2 Database
- Bean Validation
- Swagger / OpenAPI
- Gradle

## Features

- 회원 가입, 조회, 수정, 삭제 흐름을 REST API로 구현했습니다.
- 이름 검색은 QueryDSL로 처리해, 동적 쿼리 확장 가능성을 열어두었습니다.
- 모든 응답은 `success`, `data`, `error`를 기준으로 같은 형태를 유지합니다.
- 예외는 `BusinessException`과 `ErrorCode`로 분리해 한 곳에서 관리합니다.
- JWT 기반 로그인과 보호 API 인증 흐름을 구성했습니다.
- 생성일/수정일은 JPA Auditing으로 자동 기록합니다.
- Swagger UI로 API를 바로 확인할 수 있습니다.
- `local`, `test` 프로파일을 나누어 실행 환경을 분리했습니다.
- 서비스 레이어 테스트와 GitHub Actions CI로 기본 동작을 계속 검증합니다.

## Project Structure

```text
src/main/java/com/example/api
├── domain
│   └── member
│       ├── controller
│       ├── dto
│       ├── entity
│       ├── repository
│       └── service
└── global
    ├── common
    ├── config
    └── error
```

## API Docs

서버를 실행한 뒤 아래 주소에서 Swagger UI를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON 문서도 함께 제공합니다.

```text
http://localhost:8080/v3/api-docs
```

## API Endpoints

| Method | URL | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/api/members` | Public | 회원 생성 |
| `POST` | `/api/login` | Public | 로그인 및 JWT 발급 |
| `GET` | `/api/members` | Bearer Token | 전체 회원 조회 |
| `GET` | `/api/members/{userid}` | Bearer Token | 회원 단건 조회 |
| `GET` | `/api/members/search?keyword={keyword}` | Bearer Token | 회원 이름 검색 |
| `PUT` | `/api/members/{userid}` | Bearer Token | 회원 정보 수정 |
| `DELETE` | `/api/members/{userid}` | Bearer Token | 회원 삭제 |

## Request Examples

먼저 회원을 생성합니다.

```http
POST /api/members HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "userid": "hong",
  "password": "password1234",
  "username": "홍길동",
  "email": "hong@example.com"
}
```

생성한 계정으로 로그인하면 access token을 받을 수 있습니다.

```http
POST /api/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "userid": "hong",
  "password": "password1234"
}
```

로그인에 성공하면 아래처럼 `Bearer` 토큰이 내려옵니다.

```json
{
  "success": true,
  "data": {
    "grantType": "Bearer",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

이후 인증이 필요한 API는 `Authorization` 헤더에 토큰을 담아서 호출합니다.

```http
GET /api/members/hong HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

성공 응답은 아래처럼 같은 형태로 감싸서 내려갑니다.

```json
{
  "success": true,
  "data": {
    "userid": "hong",
    "username": "홍길동",
    "email": "hong@example.com"
  }
}
```

에러가 발생해도 클라이언트가 같은 규칙으로 처리할 수 있도록 코드와 메시지를 함께 내려줍니다.

```json
{
  "success": false,
  "error": {
    "code": "MEMBER_NOT_FOUND",
    "message": "회원을 찾을 수 없습니다."
  }
}
```

## Authentication

인증은 Spring Security와 JWT를 사용했습니다. 세션을 쓰지 않는 stateless 구조라서, 로그인 이후에는 클라이언트가 토큰을 들고 API를 호출하는 방식입니다.

- 회원 가입과 로그인 API는 인증 없이 접근할 수 있습니다.
- 로그인 성공 시 `Bearer` 타입의 access token을 발급합니다.
- 보호된 API는 `Authorization: Bearer {token}` 헤더가 필요합니다.
- JWT 검증은 `JwtAuthenticationFilter`에서 처리하고, 인증된 사용자 정보는 `SecurityContext`에 저장됩니다.
- 인증 실패 시 `401 Unauthorized`, 권한 부족 시 `403 Forbidden`을 반환합니다.

## H2 Console

`local` 프로파일에서는 H2 Console을 열어 데이터를 직접 확인할 수 있습니다.

```text
http://localhost:8080/h2-console
```

접속 정보:

```text
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password:
```

## Run

기본 프로파일은 `local`입니다. 로컬에서 바로 실행해볼 수 있도록 H2 인메모리 DB를 사용합니다.

```bash
./gradlew bootRun
```

프로파일을 명시해서 실행하고 싶다면 아래처럼 실행하면 됩니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Test

```bash
./gradlew test
```

현재 테스트는 회원 서비스의 조회, 검색, 생성, 수정, 삭제, 로그인, 예외 흐름을 검증합니다.

## Profile

| Profile | Description |
| --- | --- |
| `local` | 로컬 개발 환경, H2 DB, SQL 초기 데이터 사용 |
| `test` | 테스트 환경, H2 DB, SQL 초기 데이터 미사용 |

## Why

처음에는 회원 CRUD를 연습하는 작은 API로 시작했지만, 만들다 보니 “실제 프로젝트라면 어디까지 기본으로 잡아야 할까?”를 기준으로 조금씩 구조를 붙였습니다.

그래서 컨트롤러, 서비스, 리포지토리만 나누는 데서 끝내지 않고 공통 응답, 예외 코드, JWT 인증, Swagger, 테스트, CI까지 함께 정리했습니다.  
규모는 작지만, 기능이 늘어났을 때도 어디에 무엇을 둬야 할지 흐트러지지 않는 구조를 목표로 했습니다.

