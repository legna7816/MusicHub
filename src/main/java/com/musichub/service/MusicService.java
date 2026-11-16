package com.musichub.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.musichub.dao.MusicDAO;
import com.musichub.model.MusicVO;

/**
 * 음악 비즈니스 로직 서비스
 * 담당: 승민 - 파일 저장(UUID 중복방지) + CRUD 처리
 */
public class MusicService {

    private final MusicDAO musicDAO = new MusicDAO();

    // DB에 저장되는 웹 경로 prefix (실제 물리경로는 Servlet이 전달)
    private static final String UPLOAD_DIR = "resources/uploads/";

    // ====================================================================
    // [추가 기능 - 승민] 등록 결과 코드 상수
    // register() 반환값으로 실패 사유를 구분하기 위한 코드
    // ====================================================================
    /** 등록 실패 (DB 오류 등) */
    public static final int RESULT_FAIL = -1;
    /** 제목+아티스트가 동일한 곡이 이미 존재 */
    public static final int RESULT_DUPLICATE = -2;
    /** 등록 개수 제한(3개) 초과 */
    public static final int RESULT_LIMIT_EXCEEDED = -3;

    /** [추가] 1인당 음악 등록 최대 개수 */
    public static final int MAX_UPLOAD_PER_USER = 3;

    /**
     * 음악 등록
     * 음악/썸네일 파일을 서버에 저장 후 DB INSERT
     *
     * [추가된 검증 순서]
     *  1. 등록 개수 제한 체크 (3개 초과 시 RESULT_LIMIT_EXCEEDED)
     *  2. 제목+아티스트 중복 체크 (중복 시 RESULT_DUPLICATE)
     *  3. 통과하면 파일 저장 + DB 등록
     *
     * @param savePath Servlet의 getRealPath()로 구한 실제 저장 폴더
     * @return 성공 시 생성된 음악 id(양수),
     *         실패 시 RESULT_FAIL / RESULT_DUPLICATE / RESULT_LIMIT_EXCEEDED
     */
    public int register(int userId, String title, String artist, String genre,
                        Part musicPart, Part thumbnailPart, String savePath) throws Exception {

        // [추가 검증 1] 등록 개수 제한 (최대 3개) - 파일 저장 전에 먼저 체크
        if (musicDAO.countByUser(userId) >= MAX_UPLOAD_PER_USER) {
            return RESULT_LIMIT_EXCEEDED;
        }

        // [추가 검증 2] 제목+아티스트 중복 체크 (둘 다 같으면 등록 불가)
        if (musicDAO.existsByTitleAndArtist(title, artist)) {
            return RESULT_DUPLICATE;
        }

        // 음악 파일 저장 (UUID prefix로 중복 방지)
        String musicFilePath = UPLOAD_DIR + saveFile(musicPart, savePath);

        // 썸네일 저장 (선택, 없으면 null)
        String thumbnailPath = null;
        if (thumbnailPart != null && thumbnailPart.getSize() > 0) {
            thumbnailPath = UPLOAD_DIR + saveFile(thumbnailPart, savePath);
        }

        return musicDAO.insert(new MusicVO(userId, title, artist, genre, musicFilePath, thumbnailPath, 0));
    }

    /**
     * [추가] 회원의 현재 등록 개수 조회 (업로드 폼 "n/3개" 표시용)
     * @param userId 회원 id
     * @return 현재 등록한 음악 개수
     */
    public int getUploadCount(int userId) {
        return musicDAO.countByUser(userId);
    }

    /** 전체 음악 목록 조회 (최신순) */
    public List<MusicVO> getList() { return musicDAO.findAll(); }

    /** 음악 상세 조회 */
    public MusicVO getDetail(int musicId) { return musicDAO.findById(musicId); }

    /** 음악 삭제 (소프트 삭제, 본인만 가능) */
    public boolean delete(int musicId, int userId) { return musicDAO.delete(musicId, userId); }

    /**
     * 파일을 서버에 저장하고 저장된 파일명 반환
     * UUID + "_" + 원본이름 조합으로 중복 방지
     */
    private String saveFile(Part part, String savePath) throws Exception {
        String savedName = UUID.randomUUID().toString() + "_" + getFileName(part);
        File dir = new File(savePath);
        if (!dir.exists()) {
			dir.mkdirs(); // 폴더 없으면 자동 생성
		}
        part.write(savePath + File.separator + savedName);
        return savedName;
    }

    /** Content-Disposition 헤더에서 원본 파일명 추출 */
    private String getFileName(Part part) {
        String header = part.getHeader("Content-Disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2, token.length() - 1);
			}
        }
        return "unknown";
    }
}
