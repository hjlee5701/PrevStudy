# 🚀 항해99 사전 스터디 프로젝트

## 📌 프로젝트 개요  
Spring Boot 기반의 웹 애플리케이션으로, **회원 관리, 게시판, 댓글 기능**을 포함한 간단한 서비스입니다.  
**TDD(Test-Driven Development)** 를 적용하여 안정적인 코드 품질을 유지했습니다.  

---

## 🔧 기능 목록  

### 1. 회원 기능  
- 회원 가입
- 로그인

### 2️. 게시판 기능  
- 게시글 등록
- 게시글 조회
- 게시글 수정
- 게시글 삭제

### 3️. 댓글 기능  
- 댓글 작성
- 댓글 조회
- 댓글 수정
- 댓글 삭제

---

## ⚙️ 기능 설명  

- **JWT 기반 인증 및 인가**  
  - Spring Security와 Filter를 사용하여 JWT 토큰을 검증하고, 인증/인가 처리를 수행합니다.  
- **관리자**  
  - 관리자는 다른 회원의 게시글 및 댓글을 조회, 수정, 삭제할 수 있습니다.  
- **유효성 검사**  
  - 회원가입, 게시글 및 댓글 작성 시 **요청 값에 대한 유효성 검사를 수행**합니다.
- **예외 처리**  
  - 예외 발생 시 **적절한 에러 메시지와 HTTP 상태 코드**를 반환합니다.  
- **TDD 적용**  
  - **Spring Security 및 MVC 테스트 코드**를 활용하여 **단위 테스트 및 통합 테스트**를 진행하였습니다.
---

## 🛠 기술 스택  

| 영역  | 사용 기술 |
|------|---------|
| **프레임워크** | Spring Boot |
| **보안** | Spring Security, JWT |
| **데이터베이스** | JPA, Hibernate |
| **테스트** | JUnit, Mockito, Spring Boot Test |

---

## 📑 프로젝트 구조  
```
📦 src
┣ 📂 main.java
┃ ┣ 📂 com.hanghae.prevstudy
┃ ┃ ┣ 📂 domain # 도메인 계층 (비즈니스 로직)
┃ ┃ ┃ ┣ 📂 board
┃ ┃ ┃ ┣ 📂 comment
┃ ┃ ┃ ┣ 📂 member
┃ ┃ ┃ ┣ 📂 security
┃ ┃ ┣ 📂 global
┃ ┃ ┃ ┣ 📂 annotation
┃ ┃ ┃ ┣ 📂 config
┃ ┃ ┃ ┣ 📂 exception
┃ ┃ ┃ ┣ 📂 resolver # 요청 데이터 변환 및 처리
┃ ┃ ┃ ┣ 📂 response
┃ ┃ ┣ 📜 PrevStudyApplication
\
📂 test
┣ 📂 java.com.hanghae.prevstudy
┃ ┣ 📂 domain
┃ ┃ ┣ 📂 board
┃ ┃ ┣ 📂 comment
┃ ┃ ┣ 📂 common
┃ ┃ ┃ ┗ 📜 AbstractControllerTest # 공통 컨트롤러 테스트
┃ ┃ ┣ 📂 member
┃ ┃ ┣ 📂 security
┃ ┗ 📜 PrevStudyApplicationTests
