package com.haksoftware.go4lunch.repository;


import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.utils.PlacesApiHelper;

import retrofit2.Call;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    public RestaurantRepository(){}

    public static RestaurantRepository getInstance() {
        if(instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public Call<ResponseGMAP> getNearbyRestaurants(double latitude, double longitude, int radius) {

        Call<ResponseGMAP> call = PlacesApiHelper.getNearbyRestaurants(latitude, longitude, radius);
        return call;
    }
}
