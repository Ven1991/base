package com.webapp.framework.utils.tools.bean;

public class MonthDate {
	private String date;
	private String month;
	private String year;
	private int week;
	private String weekName;
	private String weekShortName;
	private int isWorkDay;
	private Object data;

	public int getWeek() {
		return this.week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getWeekName() {
		return this.weekName;
	}

	public void setWeekName(String weekName) {
		this.weekName = weekName;
	}

	public String getWeekShortName() {
		return this.weekShortName;
	}

	public void setWeekShortName(String weekShortName) {
		this.weekShortName = weekShortName;
	}

	public int getIsWorkDay() {
		return this.isWorkDay;
	}

	public void setIsWorkDay(int isWorkDay) {
		this.isWorkDay = isWorkDay;
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}