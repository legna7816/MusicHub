package com.musichub.service;

import java.util.List;

import com.musichub.dao.MusicListDAO;
import com.musichub.model.MusicVO;

/*
 * [이서윤 담당]
 * 음악 목록 Service
 *
 * 역할
 * - DAO 호출
 * - 음악 목록 조회
 * - 좋아요 여부 확인
 */
public class MusicListService {

    private MusicListDAO musicListDAO =
            new MusicListDAO();

    /*
     * 음악 목록 조회
     */
    public List<MusicVO> getMusicList(
            String keyword,
            String filter,
            String[] selectedGenres,
            String sort,
            int userId){

        return musicListDAO.getMusicList(
                keyword,
                filter,
                selectedGenres,
                sort,
                userId
        );
    }

    /*
     * 좋아요 여부 확인
     */
    public boolean isLiked(int musicId, int userId){

        return musicListDAO.isLiked(
                musicId,
                userId
        );
    }
}