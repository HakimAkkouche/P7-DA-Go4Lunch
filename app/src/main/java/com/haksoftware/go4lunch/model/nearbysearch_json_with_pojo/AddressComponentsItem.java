package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class AddressComponentsItem{
	private List<String> types;
	private String longText;
	private String shortText;
	private String languageCode;

	public List<String> getTypes(){
		return types;
	}

	public String getLongText(){
		return longText;
	}

	public String getShortText(){
		return shortText;
	}

	public String getLanguageCode(){
		return languageCode;
	}
}