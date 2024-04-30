package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import java.util.List;

public class PlacesItem{
	private String id;
	private DisplayName displayName;
	private PrimaryTypeDisplayName primaryTypeDisplayName;
	private String formattedAddress;
	private String nationalPhoneNumber;
	private float rating;
	private boolean takeout;
	private String websiteUri;
	private List<String> types;
	private List<PhotosItem> photos;
	private CurrentOpeningHours currentOpeningHours;
	private AccessibilityOptions accessibilityOptions;
	private Location location;
	private EditorialSummary editorialSummary;
	private boolean dineIn;

	public List<String> getTypes(){
		return types;
	}

	public DisplayName getDisplayName(){
		return displayName;
	}

	public float getRating(){
		return rating;
	}

	public PrimaryTypeDisplayName getPrimaryTypeDisplayName(){
		return primaryTypeDisplayName;
	}

	public boolean isTakeout(){
		return takeout;
	}

	public List<PhotosItem> getPhotos(){
		return photos;
	}

	public CurrentOpeningHours getCurrentOpeningHours(){
		return currentOpeningHours;
	}

	public String getWebsiteUri(){
		return websiteUri;
	}

	public AccessibilityOptions getAccessibilityOptions(){
		return accessibilityOptions;
	}

	public String getFormattedAddress(){
		return formattedAddress;
	}

	public Location getLocation(){
		return location;
	}

	public String getId(){
		return id;
	}

	public String getNationalPhoneNumber(){
		return nationalPhoneNumber;
	}

	public EditorialSummary getEditorialSummary(){
		return editorialSummary;
	}

	public boolean isDineIn(){
		return dineIn;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDisplayName(DisplayName displayName) {
		this.displayName = displayName;
	}

	public void setPrimaryTypeDisplayName(PrimaryTypeDisplayName primaryTypeDisplayName) {
		this.primaryTypeDisplayName = primaryTypeDisplayName;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public void setNationalPhoneNumber(String nationalPhoneNumber) {
		this.nationalPhoneNumber = nationalPhoneNumber;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public void setTakeout(boolean takeout) {
		this.takeout = takeout;
	}

	public void setWebsiteUri(String websiteUri) {
		this.websiteUri = websiteUri;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setPhotos(List<PhotosItem> photos) {
		this.photos = photos;
	}

	public void setCurrentOpeningHours(CurrentOpeningHours currentOpeningHours) {
		this.currentOpeningHours = currentOpeningHours;
	}

	public void setAccessibilityOptions(AccessibilityOptions accessibilityOptions) {
		this.accessibilityOptions = accessibilityOptions;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setEditorialSummary(EditorialSummary editorialSummary) {
		this.editorialSummary = editorialSummary;
	}

	public void setDineIn(boolean dineIn) {
		this.dineIn = dineIn;
	}
}