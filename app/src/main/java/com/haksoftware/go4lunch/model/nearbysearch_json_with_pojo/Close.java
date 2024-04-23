package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

public class Close{
	private Date date;
	private int hour;
	private int day;
	private int minute;
	private boolean truncated;

	public Date getDate(){
		return date;
	}

	public int getHour(){
		return hour;
	}

	public int getDay(){
		return day;
	}

	public int getMinute(){
		return minute;
	}

	public boolean isTruncated(){
		return truncated;
	}
}
