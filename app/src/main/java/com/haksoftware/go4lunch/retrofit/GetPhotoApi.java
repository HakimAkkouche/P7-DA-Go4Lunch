package com.haksoftware.go4lunch.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetPhotoApi {

    @GET("places/{placeId}/photos/{photoId}/media")
    Call<ResponseBody> getPlacePhoto(
            @Path("placeId") String placeId,
            @Path("photoId") String photoId,
            @Query("maxHeightPx") int maxHeightPx,
            @Query("maxWidthPx") int maxWidthPx,
            @Query("key") String apiKey
    );
}
