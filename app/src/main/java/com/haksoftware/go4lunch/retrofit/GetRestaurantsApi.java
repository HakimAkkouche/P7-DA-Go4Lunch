package com.haksoftware.go4lunch.retrofit;

import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetRestaurantsApi {

    @Headers({
            "Content-Type: application/json",
            "X-Goog-Api-Key: " + BuildConfig.GOOGLE_MAPS_API_KEY,
            "X-Goog-FieldMask: places.id," +
                    "places.displayName," +
                    "places.formattedAddress," +
                    "places.nationalPhoneNumber," +
                    "places.types," +
                    "places.location," +
                    "places.rating," +
                    "places.websiteUri," +
                    "places.dineIn," +
                    "places.currentOpeningHours," +
                    "places.editorialSummary," +
                    "places.photos"

    })
    @POST(":searchNearby")
    Call<ResponseGMAP> searchNearby(@Body NearbySearchRequest request);
}
