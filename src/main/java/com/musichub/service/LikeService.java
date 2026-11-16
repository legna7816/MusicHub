package com.musichub.service;

import com.musichub.dao.LikeDAO;

/*
 * [이서윤 담당]
 * 좋아요 기능 Service
 *
 * 역할
 * - 좋아요 토글 처리
 * - 좋아요 수 조회
 */
public class LikeService {

    private LikeDAO likeDAO = new LikeDAO();

    /*
     * 좋아요 토글
     *
     * return
     * LIKED
     * UNLIKED
     */
    public String toggleLike(int musicId, int userId) {

        boolean liked =
                likeDAO.isLiked(musicId, userId);

        if(liked) {

            likeDAO.removeLike(musicId, userId);

            return "UNLIKED";

        } else {

            likeDAO.addLike(musicId, userId);

            return "LIKED";
        }
    }

    /*
     * 좋아요 수 조회
     */
    public int getLikeCount(int musicId) {
        return likeDAO.getLikeCount(musicId);
    }

    /*
     * 좋아요 여부 확인
     */
    public boolean isLiked(int musicId, int userId) {
        return likeDAO.isLiked(musicId, userId);
    }
}