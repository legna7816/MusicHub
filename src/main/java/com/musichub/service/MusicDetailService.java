package com.musichub.service;

import com.musichub.dao.MusicDetailDAO;
import com.musichub.model.MusicVO;

/*
 * [이서윤 담당]
 * 음악 상세 페이지 Service
 *
 * 역할
 * - DAO 호출
 * - Controller와 DAO 연결
 */
public class MusicDetailService {

    private MusicDetailDAO musicDetailDAO =
            new MusicDetailDAO();

    /*
     * 음악 상세 조회
     */
    public MusicVO getMusicDetail(int musicId) {
        return musicDetailDAO.getMusicDetail(musicId);
    }

    /*
     * 좋아요 수 조회
     */
    public int getLikeCount(int musicId) {
        return musicDetailDAO.getLikeCount(musicId);
    }

    /*
     * 좋아요 여부 조회
     */
    public boolean isLiked(int musicId, int userId) {
        return musicDetailDAO.isLiked(musicId, userId);
    }

    /*
     * 플레이리스트 저장 여부 조회
     */
    public boolean isPlaylistSaved(int musicId, int userId) {
        return musicDetailDAO.isPlaylistSaved(musicId, userId);
    }
}