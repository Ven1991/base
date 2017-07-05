package com.webapp.framework.base.vo;

import com.webapp.framework.base.common.utils.BeanUtil;
import com.webapp.framework.base.common.utils.page.PageQuery;

public abstract class BaseForm extends PageQuery {
	private String userId_;
	private String userName_;
	private String menberCode_;
	private String orderName;
	private String order;
	private String excel;
	private Object user_;
	private String rqStart;
	private String rqEnd;
	private String rqStart2;
	private String rqEnd2;
	private String rqStart3;
	private String rqEnd3;
	private String rqStart4;
	private String rqEnd4;
	private Integer isPage;
	private Integer isTotal;
	private String errorFlag;

	public void setUser_(Object user) {
		if (null == user) {
			clearUser();
			return;
		}
		try {
			this.menberCode_ = ((String) BeanUtil.getValue(user, "hydm"));
			if (this.menberCode_ == null)
				this.menberCode_ = ((String) BeanUtil.getValue(user, "mbcode"));
		} catch (Exception e) {
			try {
				this.menberCode_ = ((String) BeanUtil.getValue(user, "mbcode"));
			} catch (Exception localException1) {
			}
		}
		this.userId_ = ((String) BeanUtil.getValue(user, "userId"));
		this.userName_ = ((String) BeanUtil.getValue(user, "userName"));
		if (null == this.userName_) {
			this.userName_ = ((String) BeanUtil.getValue(user, "realName"));
		}
		this.user_ = user;
	}

	private void clearUser() {
		this.user_ = null;
		this.userId_ = null;
		this.userName_ = null;
	}

	public String getUserId_() {
		return this.userId_;
	}

	public void setUserId_(String userId_) {
		this.userId_ = userId_;
	}

	public String getUserName_() {
		return this.userName_;
	}

	public void setUserName_(String userName_) {
		this.userName_ = userName_;
	}

	public String getOrderName() {
		return this.orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrder() {
		return this.order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getExcel() {
		return this.excel;
	}

	public void setExcel(String excel) {
		this.excel = excel;
	}

	public String getRqStart() {
		return this.rqStart;
	}

	public void setRqStart(String rqStart) {
		this.rqStart = rqStart;
	}

	public String getRqEnd() {
		return this.rqEnd;
	}

	public void setRqEnd(String rqEnd) {
		this.rqEnd = rqEnd;
	}

	public String getRqStart2() {
		return this.rqStart2;
	}

	public void setRqStart2(String rqStart2) {
		this.rqStart2 = rqStart2;
	}

	public String getRqEnd2() {
		return this.rqEnd2;
	}

	public void setRqEnd2(String rqEnd2) {
		this.rqEnd2 = rqEnd2;
	}

	public String getRqStart3() {
		return this.rqStart3;
	}

	public void setRqStart3(String rqStart3) {
		this.rqStart3 = rqStart3;
	}

	public String getRqEnd3() {
		return this.rqEnd3;
	}

	public void setRqEnd3(String rqEnd3) {
		this.rqEnd3 = rqEnd3;
	}

	public String getRqStart4() {
		return this.rqStart4;
	}

	public void setRqStart4(String rqStart4) {
		this.rqStart4 = rqStart4;
	}

	public String getRqEnd4() {
		return this.rqEnd4;
	}

	public void setRqEnd4(String rqEnd4) {
		this.rqEnd4 = rqEnd4;
	}

	public Integer getIsTotal() {
		return this.isTotal;
	}

	public void setIsTotal(Integer isTotal) {
		this.isTotal = isTotal;
	}

	public <T> T getUser_() {
		return (T) this.user_;
	}

	public Integer getIsPage() {
		return this.isPage;
	}

	public void setIsPage(Integer isPage) {
		this.isPage = isPage;
	}

	public String getErrorFlag() {
		return this.errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

	public String getMenberCode_() {
		return this.menberCode_;
	}

	public void setMenberCode_(String menberCode_) {
		this.menberCode_ = menberCode_;
	}
}