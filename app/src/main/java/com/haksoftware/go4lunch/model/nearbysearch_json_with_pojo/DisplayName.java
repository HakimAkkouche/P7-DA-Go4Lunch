package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

public class DisplayName{
	private String text;
	private String languageCode;

	public String getText(){
		return text;
	}

	public String getLanguageCode(){
		return languageCode;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
