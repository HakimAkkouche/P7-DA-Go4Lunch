package com.haksoftware.go4lunch.utils;


import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseDistanceMatrix;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.retrofit.Center;
import com.haksoftware.go4lunch.retrofit.Circle;
import com.haksoftware.go4lunch.retrofit.GetDetailApi;
import com.haksoftware.go4lunch.retrofit.GetRestaurantsApi;
import com.haksoftware.go4lunch.retrofit.LocationRestriction;
import com.haksoftware.go4lunch.retrofit.NearbySearchRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiHelper {

    private static final String BASE_URL = "https://places.googleapis.com/v1/";
    private static final String PLACES = "places/";
    private static final String DISTANCE_URL = "https://maps.googleapis.com/";

    public static GetRestaurantsApi createNearbySearchApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + PLACES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GetRestaurantsApi.class);
    }
    public static GetDetailApi createPictureApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GetDetailApi.class);
    }
    public static GetDetailApi createDistanceApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DISTANCE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GetDetailApi.class);
    }

    public static Call<ResponseGMAP> getNearbyRestaurants(double latitude, double longitude, int radius) {
        GetRestaurantsApi placesApi = createNearbySearchApi();

        Center center = new Center(latitude, longitude);
        Circle circle = new Circle(center, radius);
        LocationRestriction locationRestriction = new LocationRestriction(circle);

        NearbySearchRequest request = new NearbySearchRequest(Collections.singletonList("restaurant"), Locale.getDefault().getLanguage(), 20, locationRestriction);

        return placesApi.searchNearby(request);
    }

    public static Call<ResponseBody> getRestaurantPhoto(String fullUrl, int maxHeight, int mawWidth) {
        GetDetailApi pictureApi = createPictureApi();

        String regex = "places/(.*?)/photos/(.*?)$";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(fullUrl);
        String placeId = "";
        String photoId = "";
        if (matcher.find()) {
            placeId = matcher.group(1);
            photoId = matcher.group(2);

            System.out.println("PlaceId: " + placeId);
            System.out.println("PhotoId: " + photoId);
        } else {
            System.out.println("Aucune correspondance trouvée.");
        }

        return pictureApi.getPlacePhoto(placeId, photoId, maxHeight, mawWidth, BuildConfig.GOOGLE_MAPS_API_KEY);
    }
    public static Call<ResponseDistanceMatrix> getDistance(String destinationID, String origin, String unit) {
        GetDetailApi pictureApi = createDistanceApi();
        return pictureApi.getDistance(destinationID, origin, unit, BuildConfig.GOOGLE_MAPS_API_KEY);
    }

}
