package com.haksoftware.go4lunch.model;

import androidx.annotation.Nullable;

public class Restaurant {

    private String restaurantId;
    private String name;
    @Nullable
    private String phoneNumber;
    private Float rating;
    @Nullable
    private String type;
    @Nullable
    private String urlPicture;
    @Nullable
    private String webSite;
    private String address;
    private String openingHours;

    public Restaurant(String restaurantId,
                      String name,
                      @Nullable String phoneNumber,
                      Float rating,
                      @Nullable String type,
                      @Nullable String urlPicture,
                      @Nullable String webSite,
                      String address,
                      String openingHours) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.webSite = webSite;
        this.address = address;
        this.openingHours = openingHours;
    }

    //region getters/setters
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(@Nullable String webSite) {
        this.webSite = webSite;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    //endregion
}
