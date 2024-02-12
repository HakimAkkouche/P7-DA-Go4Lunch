package com.haksoftware.go4lunch.ui.colleagues;


import androidx.annotation.Nullable;

import com.haksoftware.go4lunch.model.Restaurant;

import java.util.Objects;

public class ColleagueViewState {
    private String imageUrl;
    private String name;
    @Nullable
    private Restaurant restaurant;

    public ColleagueViewState(String imageUrl, String name, @Nullable Restaurant restaurant) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.restaurant = restaurant;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(@Nullable Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColleagueViewState)) return false;
        ColleagueViewState that = (ColleagueViewState) o;
        return Objects.equals(imageUrl, that.imageUrl) && Objects.equals(name, that.name) && Objects.equals(restaurant, that.restaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl, name, restaurant);
    }
}
