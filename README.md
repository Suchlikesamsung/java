# Member Management API

Spring Boot 기반의 회원 관리 REST API 프로젝트입니다.  
JPA, QueryDSL, 공통 응답 포맷, 전역 예외 처리, Swagger 문서화를 적용해 백엔드 API 서버의 기본 구조를 연습하고 정리하는 것을 목표로 합니다.

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

- 회원 생성, 조회, 수정, 삭제 API
- QueryDSL 기반 회원 이름 검색
- 공통 API 응답 포맷
- 코드와 메시지를 포함한 표준 에러 응답 포맷
- 비즈니스 예외와 전역 예외 처리
- JPA Auditing을 활용한 생성일/수정일 관리
- Swagger UI 기반 API 문서 제공
- `local`, `test` 환경별 Spring Profile 분리
- Mockito 기반 서비스 단위 테스트

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

애플리케이션 실행 후 Swagger UI에서 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON 문서는 아래 주소에서 확인할 수 있습니다.

```text
http://localhost:8080/v3/api-docs
```

## H2 Console

`local` 프로파일에서는 H2 Console을 사용할 수 있습니다.

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

기본 프로파일은 `local`입니다.

```bash
./gradlew bootRun
```

특정 프로파일로 실행하려면 다음과 같이 실행합니다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Test

```bash
./gradlew test
```

현재 테스트는 회원 서비스의 조회, 검색, 생성, 수정, 삭제, 예외 흐름을 검증합니다.

## Profile

| Profile | Description |
| --- | --- |
| `local` | 로컬 개발 환경, H2 DB, SQL 초기 데이터 사용 |
| `test` | 테스트 환경, H2 DB, SQL 초기 데이터 미사용 |

## Why

단순 CRUD 예제를 넘어서, 실제 API 서버에서 자주 사용하는 레이어드 구조와 공통 처리 방식을 정리하기 위해 만든 프로젝트입니다.  
서비스 복잡도가 커졌을 때도 도메인, 예외, 설정, 문서화가 분리되어 유지보수하기 쉬운 구조를 목표로 합니다.

