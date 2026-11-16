package com.musichub.service;

import java.util.List;

import com.musichub.dao.RankingDAO;
import com.musichub.model.MusicVO;

/**
 * 랭킹 서비스
 * 담당: 승민 - Top5 조회 / 조회수 증가 처리
 */
public class RankingService {

    private final RankingDAO rankingDAO = new RankingDAO();

    /** 조회수 기준 Top5 음악 반환 */
    public List<MusicVO> getTop5() {
        return rankingDAO.getTop5();
    }

    /**
     * 조회수 증가 (중복 IP 방지)
     * 상세 페이지 접속 시 호출 - 같은 IP 하루 1회만 인정
     */
    public void increaseViewCount(int musicId, String ipAddress) {
        rankingDAO.increaseViewCount(musicId, ipAddress);
    }
}
