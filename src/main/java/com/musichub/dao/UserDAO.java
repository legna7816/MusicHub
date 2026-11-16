package com.musichub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.musichub.model.UserVO;
import com.musichub.util.DBUtil;

public class UserDAO {

    public int insert(UserVO user) {
        String sql = "INSERT INTO users (email, username, password, nickname) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getNickname());

            int rows = ps.executeUpdate();
            if (rows == 0) {
				return -1;
			}

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
					return keys.getInt(1);
				}
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserVO findByEmail(String email) {
        String sql = "SELECT user_id, email, username, password, nickname, "
                   + "is_deleted, created_at, updated_at "
                   + "FROM users WHERE email = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
					return mapRow(rs);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 아이디(username) 또는 이메일 어느 쪽으로 입력해도 회원을 찾는다. */
    public UserVO findByLoginId(String loginId) {
        String sql = "SELECT user_id, email, username, password, nickname, "
                   + "is_deleted, created_at, updated_at "
                   + "FROM users WHERE (email = ? OR username = ?) AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, loginId);
            ps.setString(2, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
					return mapRow(rs);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserVO findById(int userId) {
        String sql = "SELECT user_id, email, username, password, nickname, "
                   + "is_deleted, created_at, updated_at "
                   + "FROM users WHERE user_id = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
					return mapRow(rs);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(int userId, String nickname) {
        String sql = "UPDATE users SET nickname = ? "
                   + "WHERE user_id = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nickname);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? "
                   + "WHERE user_id = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(int userId) {
        // 탈퇴 시 email/username 을 user_id 기반 고유값으로 덮어써 unique 슬롯을 비운다.
        // → 같은 email/username 으로 재가입할 때 충돌이 나지 않는다.
        String sql = "UPDATE users "
                   + "SET is_deleted = 1, "
                   + "    email    = CONCAT('deleted_', user_id, '_', email), "
                   + "    username = CONCAT('del_', user_id) "
                   + "WHERE user_id = ? AND is_deleted = 0";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private UserVO mapRow(ResultSet rs) throws SQLException {
        UserVO u = new UserVO();
        u.setUserId(rs.getInt("user_id"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setDeleted(rs.getInt("is_deleted") == 1);
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
