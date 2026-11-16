package com.musichub.service;

import java.util.List;

import com.musichub.dao.PlaylistDAO;
import com.musichub.model.MusicVO;
import com.musichub.model.PlaylistVO;

/*
 * [이서윤 담당]
 * 플레이리스트 기능 Service
 *
 * 역할
 * - Controller와 DAO 사이에서 플레이리스트 기능 중간 처리
 * - 저장한 음악 목록 조회
 * - 음악 저장
 * - 음악 제거
 */
public class PlaylistService {

    private PlaylistDAO playlistDAO = new PlaylistDAO();

    /*
     * 사용자가 저장한 음악 목록 조회
     */
    public List<MusicVO> getPlaylistMusicList(int userId) {
        return playlistDAO.getPlaylistMusicList(userId);
    }

    /*
     * 플레이리스트에 음악 저장
     *
     * 반환값
     * - SAVED
     * - ALREADY
     * - ERROR
     */
    public String addMusicToPlaylist(int userId, int musicId) {
        return playlistDAO.addMusicToPlaylist(userId, musicId);
    }

    /*
     * 플레이리스트에서 음악 제거
     */
    public boolean removeMusicFromPlaylist(int userId, int musicId) {
        return playlistDAO.removeMusicFromPlaylist(userId, musicId);
    }

    public boolean removeMusic(int userId, int musicId){
        return playlistDAO.removeMusic(userId, musicId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 사용자의 플레이리스트 목록 조회
     *
     * 역할
     * - 플레이리스트 카드 출력
     * =========================================
     */
    public List<PlaylistVO> getPlaylistList(int userId){
        return playlistDAO.getPlaylistList(userId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 상세 정보 조회
     *
     * 역할
     * - Controller와 DAO 사이에서 상세 페이지 데이터를 전달
     * - 플레이리스트 기본 정보 + 대표 썸네일 + 음악 목록 포함
     * =========================================
     */
    public PlaylistVO getPlaylistDetail(int userId, int playlistId){
        return playlistDAO.getPlaylistDetail(userId, playlistId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 특정 플레이리스트에서 특정 음악 제거
     *
     * 역할
     * - 플레이리스트 상세 페이지에서 음악 한 개 제거
     * =========================================
     */
    public boolean removeMusicFromSpecificPlaylist(int userId, int playlistId, int musicId){
        return playlistDAO.removeMusicFromSpecificPlaylist(userId, playlistId, musicId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 새 플레이리스트 생성
     *
     * 역할
     * - Controller에서 받은 플레이리스트 이름을 DAO로 전달
     * =========================================
     */
    public boolean createPlaylist(int userId, String playlistName){
        return playlistDAO.createPlaylist(userId, playlistName);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 선택한 플레이리스트에 음악 저장
     *
     * 역할
     * - Controller에서 받은 playlist_id, music_id를 DAO로 전달
     * =========================================
     */
    public String addMusicToSelectedPlaylist(int userId, int playlistId, int musicId){
        return playlistDAO.addMusicToSelectedPlaylist(userId, playlistId, musicId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 삭제
     * =========================================
     */
    public boolean deletePlaylist(int userId, int playlistId){
        return playlistDAO.deletePlaylist(userId, playlistId);
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 이름 수정
     * =========================================
     */
    public boolean updatePlaylistName(int userId, int playlistId, String playlistName){
        return playlistDAO.updatePlaylistName(userId, playlistId, playlistName);
    }
}