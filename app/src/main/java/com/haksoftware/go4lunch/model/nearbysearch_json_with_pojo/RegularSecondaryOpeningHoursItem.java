package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class RegularSecondaryOpeningHoursItem{
	private String secondaryHoursType;
	private boolean openNow;
	private List<PeriodsItem> periods;
	private List<String> weekdayDescriptions;

	public String getSecondaryHoursType(){
		return secondaryHoursType;
	}

	public boolean isOpenNow(){
		return openNow;
	}

	public List<PeriodsItem> getPeriods(){
		return periods;
	}

	public List<String> getWeekdayDescriptions(){
		return weekdayDescriptions;
	}
}