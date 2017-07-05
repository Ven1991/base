package com.webapp.framework.core.beans;

import java.io.Serializable;

public abstract class BaseUserForm implements Serializable {
	private String userId;
	private String userName;
	private String tokenId;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTokenId() {
		return this.tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
}