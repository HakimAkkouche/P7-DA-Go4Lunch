package com.haksoftware.go4lunch.utils;

import com.haksoftware.go4lunch.BuildConfig;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacePhotoResponse;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.retrofit.Center;
import com.haksoftware.go4lunch.retrofit.Circle;
import com.haksoftware.go4lunch.retrofit.GetPhotoApi;
import com.haksoftware.go4lunch.retrofit.GetRestaurantsApi;
import com.haksoftware.go4lunch.retrofit.LocationRestriction;
import com.haksoftware.go4lunch.retrofit.NearbySearchRequest;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiHelper {

    private static final String BASE_URL = "https://places.googleapis.com/v1/";

    private static final String PLACES_URL = "https://places.googleapis.com/v1/places/";

    public static GetRestaurantsApi createApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PLACES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GetRestaurantsApi.class);
    }
    public static GetPhotoApi createPictureApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GetPhotoApi.class);
    }

    public static Call<ResponseGMAP> getNearbyRestaurants(double latitude, double longitude, int radius) {
        GetRestaurantsApi placesApi = createApi();

        Center center = new Center(latitude, longitude);
        Circle circle = new Circle(center, radius);
        LocationRestriction locationRestriction = new LocationRestriction(circle);

        NearbySearchRequest request = new NearbySearchRequest(Arrays.asList("restaurant"), Locale.getDefault().getLanguage(), 20, locationRestriction);

        return placesApi.searchNearby(request);
    }

    public static Call<ResponseBody> getRestaurantPhoto(String fullUrl, int maxHeight, int mawWidth) {
        GetPhotoApi pictureApi = createPictureApi();

        String regex = "places/(.*?)/photos/(.*?)$";
        Pattern pattern = Pattern.compile(regex);

        // Créer un objet Matcher avec l'entrée et le modèle
        Matcher matcher = pattern.matcher(fullUrl);
        String placeId = "";
        String photoId = "";
        // Vérifier si une correspondance est trouvée
        if (matcher.find()) {
            // Récupérer le placeId et le photoId à partir des groupes capturés
            placeId = matcher.group(1);
            photoId = matcher.group(2);

            // Afficher les résultats
            System.out.println("PlaceId: " + placeId);
            System.out.println("PhotoId: " + photoId);
        } else {
            System.out.println("Aucune correspondance trouvée.");
        }

        return pictureApi.getPlacePhoto(placeId, photoId, maxHeight, mawWidth, BuildConfig.GOOGLE_MAPS_API_KEY);
    }
}
