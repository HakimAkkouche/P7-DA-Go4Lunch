package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class PhotosItem{
	private List<AuthorAttributionsItem> authorAttributions;
	private int widthPx;
	private int heightPx;
	private String name;

	public List<AuthorAttributionsItem> getAuthorAttributions(){
		return authorAttributions;
	}

	public int getWidthPx(){
		return widthPx;
	}

	public int getHeightPx(){
		return heightPx;
	}

	public String getName(){
		return name;
	}
}