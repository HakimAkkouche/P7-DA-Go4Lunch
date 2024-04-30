package com.haksoftware.go4lunch.model;

public class LikedRestaurant {
    private String restaurantId;

    public LikedRestaurant() {
        // Constructeur par défaut requis pour Firebase
    }

    public LikedRestaurant(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
