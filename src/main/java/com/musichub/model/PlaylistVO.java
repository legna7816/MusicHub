package com.musichub.model;

import java.util.ArrayList;
import java.util.List;

/*
 * [이서윤 담당]
 * 플레이리스트 정보 VO
 *
 * 역할
 * - playlists 테이블의 플레이리스트 기본 정보 저장
 * - 플레이리스트 카드 출력에 필요한 대표 썸네일 목록 저장
 * - 플레이리스트 상세 페이지에서 음악 목록을 함께 전달할 수 있도록 musicList 보관
 *
 * 참고
 * - DB 테이블은 수정하지 않음
 * - playlists.name 컬럼을 playlistName으로 사용
 * - thumbnailList는 대표 이미지 4분할 카드 출력용
 */
public class PlaylistVO {

    private int playlistId;
    private int userId;
    private String playlistName;
    private String createdAt;

    /*
     * [이서윤 담당]
     * 플레이리스트 카드 대표 썸네일 목록
     *
     * - 음악이 4개 이상이면 처음 추가된 음악 4개의 썸네일 사용
     * - 음악이 1~3개이면 첫 번째 음악 썸네일을 대표 이미지처럼 사용
     * - 음악이 없거나 썸네일이 없으면 JSP에서 기본 음표 이미지 출력
     */
    private List<String> thumbnailList = new ArrayList<>();

    /*
     * [이서윤 담당]
     * 플레이리스트 상세 페이지에서 출력할 음악 목록
     */
    private List<MusicVO> musicList = new ArrayList<>();

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getThumbnailList() {
        return thumbnailList;
    }

    public void setThumbnailList(List<String> thumbnailList) {
        this.thumbnailList = thumbnailList;
    }

    public List<MusicVO> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicVO> musicList) {
        this.musicList = musicList;
    }
}