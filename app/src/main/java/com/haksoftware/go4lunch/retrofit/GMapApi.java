package com.haksoftware.go4lunch.retrofit;

import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GMapApi {
    @GET("nearbysearch/json")
    Call<Root> getAllRestaurant(
            //   @Query("keyword") String keyword,
            @Query("location") String Location,
            @Query("radius") int  radius,
            @Query("type") String Type,
            @Query("key") String KeyMap
    );
}
