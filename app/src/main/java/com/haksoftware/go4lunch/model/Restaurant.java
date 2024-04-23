package com.haksoftware.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class Restaurant implements Parcelable {

    private String restaurantId;
    private String name;
    private String address;
    @Nullable
    private String phoneNumber;
    @Nullable
    private String type;
    private float rating;
    private double latitude;
    private double longitude;
    private String urlWebsite;
    private String openingHours;
    private String editorialSummary;
    @Nullable
    private String urlPicture;
    private int countColleagues;

    // Constructeur sans argument nécessaire pour la désérialisation Firebase
    public Restaurant() {
    }
    public Restaurant(String restaurantId,
                      String name,
                      @Nullable String phoneNumber,
                      float rating,
                      @Nullable String type,
                      @Nullable String urlPicture,
                      String address,
                      String openingHours,
                      String editorialSummary,
                      String urlWebsite,
                      double latitude,
                      double longitude) {
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
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    // Parcelable implementation
    protected Restaurant(Parcel in) {
        restaurantId = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
        rating = in.readFloat();
        type = in.readString();
        urlPicture = in.readString();
        address = in.readString();
        openingHours = in.readString();
        editorialSummary = in.readString();
        urlWebsite = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantId);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeDouble(rating);
        dest.writeString(type);
        dest.writeString(urlPicture);
        dest.writeString(address);
        dest.writeString(openingHours);
        dest.writeString(editorialSummary);
        dest.writeString(urlWebsite);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    //region getters
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

    @Nullable
    public String getEditorialSummary() {
        return editorialSummary;
    }

    public String getUrlWebsite() {
        return urlWebsite;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCountColleagues() {
        return countColleagues;
    }

    public void setCountColleagues(int countColleagues) {
        this.countColleagues = countColleagues;
    }

    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        Restaurant that = (Restaurant) o;
        return Float.compare(that.rating, rating) == 0
                && Objects.equals(restaurantId, that.restaurantId)
                && Objects.equals(name, that.name)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(type, that.type)
                && Objects.equals(urlPicture, that.urlPicture)
                && Objects.equals(address, that.address)
                && Objects.equals(openingHours, that.openingHours)
                && Objects.equals(editorialSummary, that.editorialSummary)
                && Objects.equals(latitude, that.latitude)
                && Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, name, phoneNumber, rating, type, urlPicture, address, openingHours, editorialSummary, latitude, longitude);
    }
}
