package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class ResponseGMAP{
	private List<PlacesItem> places;

	public List<PlacesItem> getPlaces(){
		return places;
	}

	public void setPlaces(List<PlacesItem> places) {
		this.places = places;
	}
}