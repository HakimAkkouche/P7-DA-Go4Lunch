package com.haksoftware.go4lunch.ui.restaurant_list;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class RestaurantViewState {
    private String restaurantId;
    private String name;
    @Nullable
    private String phoneNumber;
    private float rating;
    @Nullable
    private String type;
    @Nullable
    private String urlPicture;
    private String address;
    private String openingHours;
    private String editorialSummary;

    private LatLng latLng;
    private int distance;
    private int colleaguesCount;

    public RestaurantViewState(String restaurantId,
                               String name,
                               @Nullable String phoneNumber,
                               float rating,
                               @Nullable String type,
                               @Nullable String urlPicture,
                               String address,
                               String openingHours,
                               String editorialSummary,
                               LatLng latLng,
                               int distance,
                               int colleaguesCount) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.address = address;
        this.openingHours = openingHours;
        this.editorialSummary = editorialSummary;
        this.latLng = latLng;
        this.distance= distance;
        this.colleaguesCount = colleaguesCount;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getEditorialSummary() {
        return editorialSummary;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getDistance() {
        return distance;
    }

    public int getColleaguesCount() {
        return colleaguesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestaurantViewState)) return false;
        RestaurantViewState that = (RestaurantViewState) o;
        return Float.compare(that.rating, rating) == 0 && distance == that.distance && colleaguesCount == that.colleaguesCount && Objects.equals(restaurantId, that.restaurantId) && Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(type, that.type) && Objects.equals(urlPicture, that.urlPicture) && Objects.equals(address, that.address) && Objects.equals(openingHours, that.openingHours) && Objects.equals(editorialSummary, that.editorialSummary) && Objects.equals(latLng, that.latLng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, name, phoneNumber, rating, type, urlPicture, address, openingHours, editorialSummary, latLng, distance, colleaguesCount);
    }
}
