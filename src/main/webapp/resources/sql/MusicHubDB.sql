-- =========================
-- MusicHub 통합 데이터베이스
-- =========================

CREATE DATABASE IF NOT EXISTS MusicHubDB DEFAULT CHARACTER SET utf8mb4;
USE MusicHubDB;

-- =========================
-- 공통 테이블 (팀 공통 사용)
-- =========================

-- 회원 테이블
-- 담당: 유저 파트 조원
CREATE TABLE users (
    user_id INT NOT NULL AUTO_INCREMENT,       -- 회원 고유 식별자
    email VARCHAR(100) NOT NULL,               -- 로그인용 이메일
    username VARCHAR(30) NOT NULL,             -- 로그인용 아이디, 영문/숫자/밑줄 4~20자
    password VARCHAR(255) NOT NULL,            -- BCrypt 해시 비밀번호
    nickname VARCHAR(50) NOT NULL,             -- 화면 표시용 닉네임
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,  -- 소프트 삭제 플래그 (0: 활성, 1: 탈퇴)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,    -- 가입 일시
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP, -- 마지막 수정 일시
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email),      -- 이메일 중복 가입 방지
    UNIQUE KEY uk_users_username (username) -- 아이디 중복 가입 방지
) CHARSET=utf8mb4;

-- 게시판 테이블
-- 담당: 유저 파트 조원
CREATE TABLE boards (
    board_id INT NOT NULL AUTO_INCREMENT,      -- 게시글 고유 식별자
    user_id INT NOT NULL,                      -- 회원 ID (users.user_id)
    title VARCHAR(200) NOT NULL,               -- 게시글 제목
    content TEXT NOT NULL,                     -- 게시글 본문
    view_count INT NULL DEFAULT 0,             -- 조회수
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (board_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
) DEFAULT CHARSET=utf8mb4;

-- 게시판 댓글 테이블
-- 담당: 이상빈 (최신본 반영)
CREATE TABLE board_comments (
    comment_id INT          NOT NULL AUTO_INCREMENT,                    -- 댓글 고유 식별자 (PK, 자동 증가)
    board_id   INT          NOT NULL,                                   -- 대상 게시글 (boards.board_id)
    user_id    INT          NOT NULL,                                   -- 작성자 (users.user_id)
    content    VARCHAR(500) NOT NULL,                                   -- 댓글 내용
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,         -- 작성 일시
    PRIMARY KEY (comment_id),
    FOREIGN KEY (board_id) REFERENCES boards(board_id) ON DELETE CASCADE,  -- 게시글 삭제 시 댓글도 함께 삭제
    FOREIGN KEY (user_id)  REFERENCES users(user_id)                       -- 작성자 참조 (탈퇴 회원 댓글은 보존 → CASCADE 없음)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;                                -- 버전 무관 FK 동작 + 한글/이모지 지원


-- =========================
-- 승민 담당 테이블
-- 음악 등록 / 30초 미리듣기 / 랭킹 기능에서 사용
-- =========================

-- 음악 테이블
-- 업로드된 음악 정보를 저장하는 핵심 테이블
CREATE TABLE musics (
    id INT NOT NULL AUTO_INCREMENT,                     -- 음악 고유 식별자
    user_id INT NOT NULL,                               -- 업로더 회원 ID (users.user_id 참조)
    title VARCHAR(200) NOT NULL,                        -- 음악 제목
    artist VARCHAR(100) NOT NULL,                       -- 아티스트명
    genre VARCHAR(50) DEFAULT NULL,                     -- 장르 (pop, rock, hiphop 등)
    file_path VARCHAR(500) NOT NULL,                    -- 서버에 저장된 음악 파일 경로 (UUID prefix 사용)
    thumbnail_path VARCHAR(500) DEFAULT NULL,           -- 앨범 커버 이미지 경로
    duration INT DEFAULT 0,                             -- 음악 전체 재생 시간 (초 단위)
    view_count INT NOT NULL DEFAULT 0,                  -- 조회수 (랭킹 계산 기준)
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,           -- 소프트 삭제 플래그 (0: 활성, 1: 삭제)
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,       -- 업로드 일시
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                         ON UPDATE CURRENT_TIMESTAMP,             -- 마지막 수정 일시
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4;

-- 음악 조회 기록 테이블
-- 랭킹 산정 시 동일 IP 중복 조회를 방지하기 위한 테이블
-- 같은 IP는 하루에 1번만 조회수로 인정됨
CREATE TABLE music_views (
    view_id     INT         NOT NULL AUTO_INCREMENT,          -- 조회 기록 고유 식별자
    music_id    INT         NOT NULL,                         -- 조회된 음악 ID (musics.id 참조)
    ip_address  VARCHAR(45) NOT NULL,                         -- 조회자 IP 주소 (IPv6 대비 45자)
    viewed_slot CHAR(12)    NOT NULL,                         -- 조회 시각 슬롯 (연월일시분, 1분 1회 제한 기준)
    viewed_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 조회 일시 (상세 기록용)
    PRIMARY KEY (view_id),
    -- (음악ID + IP + 분단위시각) 조합으로 1분에 1회만 허용
    -- viewed_slot 컬럼을 별도로 두어 MySQL 버전에 무관하게 동작
    UNIQUE KEY uk_music_views (music_id, ip_address, viewed_slot),
    FOREIGN KEY (music_id) REFERENCES musics(id) ON DELETE CASCADE
) DEFAULT CHARSET=utf8mb4;


-- =========================
-- 이서윤 담당 테이블
-- 좋아요 / 댓글 / 플레이리스트 / 추천 기능에서 사용
-- =========================

-- 음악 좋아요 테이블
CREATE TABLE music_likes (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    music_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_like (music_id, user_id),
    FOREIGN KEY (music_id) REFERENCES musics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 음악 댓글 테이블
CREATE TABLE music_comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    music_id INT NOT NULL,
    user_id INT NOT NULL,
    writer VARCHAR(50),
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (music_id) REFERENCES musics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 플레이리스트 테이블
CREATE TABLE playlists (
    playlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 플레이리스트 음악 매핑 테이블
CREATE TABLE playlist_musics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    playlist_id INT NOT NULL,
    music_id INT NOT NULL,
    UNIQUE KEY unique_playlist_music (playlist_id, music_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (music_id) REFERENCES musics(id) ON DELETE CASCADE
);
