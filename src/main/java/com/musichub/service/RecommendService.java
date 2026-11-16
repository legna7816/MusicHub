package com.musichub.service;

import java.util.List;

import com.musichub.dao.RecommendDAO;
import com.musichub.model.MusicVO;

/*
 * [이서윤 담당]
 * 사용자 추천 기능 Service
 *
 * 역할
 * - Controller와 DAO 사이에서 추천 목록 조회를 중간 처리
 * - 현재는 DAO 호출 중심이지만, 추후 추천 점수 계산 로직 확장 가능
 */
public class RecommendService {

    private RecommendDAO recommendDAO = new RecommendDAO();

    /*
     * 좋아하는 아티스트 기반 추천 목록
     */
    public List<MusicVO> getArtistRecommendList(int userId) {
        return recommendDAO.getArtistRecommendList(userId);
    }

    /*
     * 좋아하는 장르 기반 추천 목록
     */
    public List<MusicVO> getGenreRecommendList(int userId) {
        return recommendDAO.getGenreRecommendList(userId);
    }

    /*
     * 인기 음악 추천 목록
     */
    public List<MusicVO> getPopularRecommendList(int userId) {
        return recommendDAO.getPopularRecommendList(userId);
    }
}