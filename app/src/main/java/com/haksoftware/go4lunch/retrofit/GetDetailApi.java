package com.haksoftware.go4lunch.retrofit;

import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseDistanceMatrix;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDetailApi {


    @GET("places/{placeId}/photos/{photoId}/media")
    Call<ResponseBody> getPlacePhoto(
            @Path("placeId") String placeId,
            @Path("photoId") String photoId,
            @Query("maxHeightPx") int maxHeightPx,
            @Query("maxWidthPx") int maxWidthPx,
            @Query("key") String apiKey
    );
    @GET("maps/api/distancematrix/json")
    Call<ResponseDistanceMatrix> getDistance(
            @Query("destinations") String destination,
            @Query("origins") String origin,
            @Query("units") String units,
            @Query("key") String apiKey
    );

}
