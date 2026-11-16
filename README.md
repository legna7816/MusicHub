# MusicHub 🎵

음악을 공유하고 30초 미리듣기, 추천, 랭킹을 즐기는 웹 서비스입니다.

## 기술 스택

| 기술 | 설명 |
|---|---|
| Java 11 | 전체 백엔드 로직 |
| Servlet / JSP (Jakarta EE) | `@WebServlet`, `HttpSession`, `@WebFilter` 기반 MVC 구조 |
| Apache Tomcat 10.1 | 웹 애플리케이션 서버 |
| MySQL 8 | 데이터 저장 (users, musics, boards 등) |
| JDBC (DriverManager) | 순수 JDBC로 DB 연동 |
| BCrypt (jbcrypt) | 회원가입/로그인 비밀번호 해시 |
| JSTL | JSP 화면 로직 (`<c:if>` 등) |

## 주요 기능

- **회원 관리** (이상빈): 회원가입/로그인/로그아웃, BCrypt 비밀번호 해시, 세션 인증, 마이페이지(정보수정·탈퇴)
- **자유게시판** (이상빈): 글 CRUD, 댓글, 좋아요
- **음악**: 등록/목록/상세/삭제, 30초 미리듣기
- **랭킹 · 추천**: 조회수 기반 Top5 랭킹, 음악 추천
- **플레이리스트**
- **다국어(i18n)**: 한국어/영어 전환

## 실행 방법

1. MySQL에서 DB 생성
   ```sql
   src/main/webapp/resources/sql/MusicHubDB.sql 실행
   ```

2. DB 접속 정보 설정
   ```bash
   cp src/main/java/db.properties.example src/main/java/db.properties
   ```
   `db.properties`를 열어 본인 MySQL 계정/비밀번호로 수정

3. Eclipse에서 Dynamic Web Project로 열고 Tomcat 10.1에 배포 후 실행

## 패키지 구조

```
com.musichub
├── controller   # 서블릿 (요청 처리)
├── service      # 비즈니스 로직
├── dao          # DB 접근 (JDBC)
├── model        # VO (데이터 객체)
├── filter       # 인증 필터
└── util         # DBUtil 등 공통 유틸
```
