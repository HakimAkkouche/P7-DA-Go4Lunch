package com.haksoftware.go4lunch.repository;


import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Root;
import com.haksoftware.go4lunch.retrofit.GMapApi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    public RestaurantRepository(){}

    public static RestaurantRepository getInstance() {
        if(instance == null) {
            instance = new RestaurantRepository();
        }
        return instance;
    }

    public Call<Root> getNearbyRestaurants(String url, String location, int radius, String type, String key) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GMapApi gMapApi = retrofit.create(GMapApi.class);

        Call<Root> call = gMapApi.getAllRestaurant(location, radius, type, key);
        return call;
    }
}
