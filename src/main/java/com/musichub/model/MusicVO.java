package com.musichub.model;

import java.sql.Timestamp;

/**
 * [통합 MusicVO] - musics 테이블 매핑 VO
 *
 * [머지 메모 - 중요]
 * 승민(랭킹)과 이서윤(추천)이 같은 패키지에 서로 다른 MusicVO를 만들어 충돌 발생.
 * → 두 클래스의 필드/메서드를 모두 포함한 하나의 통합 VO로 병합.
 *
 * 양쪽 코드가 수정 없이 동작하도록 별칭(alias) 메서드 제공:
 *  - 승민 코드:   getId() / getViewCount()        (DB 컬럼명 기준)
 *  - 이서윤 코드: getMusicId() / getPlayCount()   (화면 기준 이름)
 *  → 내부적으로 같은 필드(id, viewCount)를 공유한다.
 */
public class MusicVO {

    // ===== musics 테이블 컬럼 매핑 =====
    private int       id;               // PK (이서윤 코드에서는 musicId로 접근)
    private int       userId;           // 업로더 회원 ID
    private String    title;            // 제목
    private String    artist;           // 아티스트
    private String    genre;            // 장르
    private String    filePath;         // 음악 파일 경로 (30초 미리듣기에 사용)
    private String    thumbnailPath;    // 썸네일 경로
    private int       duration;         // 재생 시간(초)
    private int       viewCount;        // 조회수 (이서윤 코드에서는 playCount로 접근)
    private boolean   deleted;          // 소프트 삭제 여부
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ===== 화면 출력용 (DB 컬럼 아님) =====
    private String  uploaderNickname;   // 업로더 닉네임 (JOIN 조회)
    private boolean liked;              // 현재 사용자의 좋아요 여부 (이서윤 추천 기능)

    public MusicVO() {}

    /** 음악 등록용 생성자 (승민) */
    public MusicVO(int userId, String title, String artist, String genre,
                   String filePath, String thumbnailPath, int duration) {
        this.userId        = userId;
        this.title         = title;
        this.artist        = artist;
        this.genre         = genre;
        this.filePath      = filePath;
        this.thumbnailPath = thumbnailPath;
        this.duration      = duration;
    }

    /** 추천 목록용 생성자 (이서윤 - 기존 코드 호환) */
    public MusicVO(int musicId, String title, String artist, String genre, int playCount) {
        this.id        = musicId;
        this.title     = title;
        this.artist    = artist;
        this.genre     = genre;
        this.viewCount = playCount;
    }

    // ===== 기본 getter/setter (승민 코드 기준) =====
    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }
    public int getUserId()                   { return userId; }
    public void setUserId(int userId)        { this.userId = userId; }
    public String getTitle()                 { return title; }
    public void setTitle(String title)       { this.title = title; }
    public String getArtist()                { return artist; }
    public void setArtist(String artist)     { this.artist = artist; }
    public String getGenre()                 { return genre; }
    public void setGenre(String genre)       { this.genre = genre; }
    public String getFilePath()              { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getThumbnailPath()                   { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
    public int getDuration()                 { return duration; }
    public void setDuration(int duration)    { this.duration = duration; }
    public int getViewCount()                { return viewCount; }
    public void setViewCount(int viewCount)  { this.viewCount = viewCount; }
    public boolean isDeleted()               { return deleted; }
    public void setDeleted(boolean deleted)  { this.deleted = deleted; }
    public Timestamp getCreatedAt()               { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt()               { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public String getUploaderNickname()                      { return uploaderNickname; }
    public void setUploaderNickname(String uploaderNickname) { this.uploaderNickname = uploaderNickname; }

    // ===== 별칭 메서드 (이서윤 코드 호환 - 같은 필드를 다른 이름으로 접근) =====
    /** musicId = id (이서윤 추천/플레이리스트 코드용 별칭) */
    public int getMusicId()                  { return id; }
    public void setMusicId(int musicId)      { this.id = musicId; }

    /** playCount = viewCount (이서윤 추천 코드용 별칭) */
    public int getPlayCount()                { return viewCount; }
    public void setPlayCount(int playCount)  { this.viewCount = playCount; }

    /** 좋아요 여부 (이서윤 추천 기능) */
    public boolean isLiked()                 { return liked; }
    public void setLiked(boolean liked)      { this.liked = liked; }

    @Override
    public String toString() {
        return "MusicVO{id=" + id + ", title=" + title + ", viewCount=" + viewCount + "}";
    }
}
