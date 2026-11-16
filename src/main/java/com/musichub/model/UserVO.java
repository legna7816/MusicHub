package com.musichub.model;

import java.sql.Timestamp;

public class UserVO {

    private int userId;
    private String email;
    private String username;
    private String password;
    private String nickname;
    private boolean deleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserVO() { }

    public UserVO(String email, String username, String password, String nickname) {
        this.email    = email;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
    public String toString() {
        return "UserVO{userId=" + userId + ", email=" + email
             + ", username=" + username
             + ", nickname=" + nickname
             + ", deleted=" + deleted + "}";
    }
}
