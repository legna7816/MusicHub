package com.musichub.service;

import org.mindrot.jbcrypt.BCrypt;

import com.musichub.dao.UserDAO;
import com.musichub.model.UserVO;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    private static final int BCRYPT_COST = 12;

    public enum SignUpResult {
        SUCCESS,            // 가입 성공
        DUPLICATE_EMAIL,    // 이미 사용 중인 이메일
        DUPLICATE_USERNAME, // 이미 사용 중인 아이디
        INVALID_INPUT,      // 입력값 형식 오류
        DB_ERROR            // DB 처리 실패
    }

    /**
     * 회원가입 처리.
     * 1) 입력값 검증 → 2) 이메일·아이디 중복 검사 → 3) 비밀번호 해시 → 4) 저장
     */
    public SignUpResult signUp(String email, String username,
                               String rawPassword, String nickname) {
        if (!isValidEmail(email)
                || !isValidUsername(username)
                || rawPassword == null || rawPassword.length() < 8
                || nickname == null || nickname.trim().isEmpty()) {
            return SignUpResult.INVALID_INPUT;
        }

        if (userDAO.existsByEmail(email)) {
            return SignUpResult.DUPLICATE_EMAIL;
        }
        if (userDAO.existsByUsername(username)) {
            return SignUpResult.DUPLICATE_USERNAME;
        }

        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_COST));

        UserVO user = new UserVO(email.trim(), username.trim(), hashed, nickname.trim());
        int newId = userDAO.insert(user);

        return (newId > 0) ? SignUpResult.SUCCESS : SignUpResult.DB_ERROR;
    }

    /**
     * 아이디(username) 또는 이메일 어느 쪽으로 입력해도 인증한다.
     * @return 일치하면 해당 회원 VO, 아니면 null
     */
    public UserVO authenticate(String loginId, String rawPassword) {
        UserVO user = userDAO.findByLoginId(loginId);
        if (user == null) {
			return null;
		}
        boolean match = BCrypt.checkpw(rawPassword, user.getPassword());
        return match ? user : null;
    }

    /** 비밀번호 변경 결과 코드. */
    public enum PasswordResult {
        SUCCESS,            // 변경 성공
        WRONG_CURRENT,      // 현재 비밀번호 불일치
        INVALID_NEW,        // 새 비밀번호 형식 오류(8자 미만 등)
        DB_ERROR            // DB 처리 실패
    }

    public PasswordResult changePassword(int userId,
                                         String currentRawPassword,
                                         String newRawPassword) {
        UserVO user = userDAO.findById(userId);
        if (user == null) {
			return PasswordResult.DB_ERROR;
		}

        if (!BCrypt.checkpw(currentRawPassword, user.getPassword())) {
            return PasswordResult.WRONG_CURRENT;
        }
        if (newRawPassword == null || newRawPassword.length() < 8) {
            return PasswordResult.INVALID_NEW;
        }
        String hashed = BCrypt.hashpw(newRawPassword, BCrypt.gensalt(BCRYPT_COST));
        boolean ok = userDAO.updatePassword(userId, hashed);
        return ok ? PasswordResult.SUCCESS : PasswordResult.DB_ERROR;
    }

    private boolean isValidEmail(String email) {
        return email != null
            && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    /** 아이디 형식 검사: 영문/숫자/밑줄 4~20자. */
    private boolean isValidUsername(String username) {
        return username != null
            && username.matches("^[A-Za-z0-9_]{4,20}$");
    }
}
