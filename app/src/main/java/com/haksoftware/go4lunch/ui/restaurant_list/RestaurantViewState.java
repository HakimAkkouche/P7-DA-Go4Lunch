package com.haksoftware.go4lunch.ui.restaurant_list;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class RestaurantViewState {
    private final String restaurantId;
    private final String name;
    @Nullable
    private final String phoneNumber;
    private final float rating;
    @Nullable
    private final String type;
    @Nullable
    private final String urlPicture;
    private final String address;
    private final String openingHours;
    private final String editorialSummary;
    private final String urlWebsite;
    private final LatLng latLng;
    private final MutableLiveData<String> distance = new MutableLiveData<>();
    private final MutableLiveData<String> colleaguesCount = new MutableLiveData<>();

    public RestaurantViewState(String restaurantId,
                               String name,
                               @Nullable String phoneNumber,
                               float rating,
                               @Nullable String type,
                               @Nullable String urlPicture,
                               String address,
                               String openingHours,
                               String editorialSummary,
                               String urlWebsite,
                               LatLng latLng,
                               String colleaguesCount) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.address = address;
        this.openingHours = openingHours;
        this.editorialSummary = editorialSummary;
        this.urlWebsite = urlWebsite;
        this.latLng = latLng;
        this.colleaguesCount.setValue(colleaguesCount);
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

    public String getUrlWebsite() {
        return urlWebsite;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public MutableLiveData<String> getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance.postValue(distance);
    }
    public void setColleaguesCount(String colleaguesCount) {
        this.colleaguesCount.postValue(colleaguesCount);
    }
    public String getColleaguesCount() {
        return colleaguesCount.getValue();
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
