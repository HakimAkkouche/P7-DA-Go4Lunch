package com.haksoftware.go4lunch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.Place;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.ui.restaurant_list.RestaurantViewState;

import java.util.Objects;

public class Utils {
    public static Restaurant createRestaurantFromResponseGMAP(Context context, PlacesItem result){

        StringBuilder type = new StringBuilder();
        for (String s :
                result.getTypes()) {
            type.append(s).append(", ");

        }
        String urlPhoto = "";
        if(result.getPhotos() != null) {
            urlPhoto = result.getPhotos().get(0).getName();
        }

        String urlWebsite = "";
        if(result.getWebsiteUri() != null) {
            urlWebsite = result.getWebsiteUri();
        }
        String openingHours = context.getString(R.string.openning_hours) + " ";
        if( result.getCurrentOpeningHours() != null) {

            openingHours += result.getCurrentOpeningHours().getPeriods().get(0).getOpen().getHour() +
                    "h" +
                    (result.getCurrentOpeningHours().getPeriods().get(0).getOpen().getMinute() == 0 ? "00" :
                            String.valueOf(result.getCurrentOpeningHours().getPeriods().get(0).getOpen().getMinute())) +
                    " - " +
                    result.getCurrentOpeningHours().getPeriods().get(0).getClose().getHour() +
                    "h" +
                    (result.getCurrentOpeningHours().getPeriods().get(0).getClose().getMinute() == 0 ? "00" :
                            result.getCurrentOpeningHours().getPeriods().get(0).getClose().getMinute());
        }

        String editorialSummary = result.getEditorialSummary() != null ?  result.getEditorialSummary().getText() : context.getString(R.string.summary_not_available);

        LatLng restaurantLatLng = new LatLng(result.getLocation().getLatitude(), result.getLocation().getLongitude());


        return new Restaurant(result.getId(),
                result.getDisplayName().getText(),
                result.getNationalPhoneNumber(),
                result.getRating(),
                type.toString(), urlPhoto,
                result.getFormattedAddress(),
                openingHours,
                editorialSummary,
                urlWebsite,
                restaurantLatLng);

    }
    public static Restaurant createRestaurantFromPlace(Context context, Place place){

        StringBuilder type = new StringBuilder();
        if(place.getPlaceTypes() != null) {
            for (String s :
                    place.getPlaceTypes()) {
                type.append(s).append(", ");

            }
        }
        String urlPhoto = "";
        if(place.getPhotoMetadatas() != null) {
            if(place.getPhotoMetadatas().get(0).getAuthorAttributions() != null) {
                urlPhoto = Objects.requireNonNull(place.getPhotoMetadatas().get(0).getAuthorAttributions()).asList().get(0).getPhotoUri();
            }
        }

        String urlWebsite = "";
        if(place.getWebsiteUri() != null) {
            urlWebsite = place.getWebsiteUri().getPath();
        }


        String openingHours = context.getString(R.string.openning_hours) + " " +
                Objects.requireNonNull(Objects.requireNonNull(place.getCurrentOpeningHours()).getPeriods().get(0).getOpen()).getTime().getHours() +
                "h" +
                (Objects.requireNonNull(place.getCurrentOpeningHours().getPeriods().get(0).getOpen()).getTime().getMinutes() == 0 ? "00" :
                String.valueOf(Objects.requireNonNull(place.getCurrentOpeningHours().getPeriods().get(0).getOpen()).getTime().getMinutes()))+
                " - " +
                Objects.requireNonNull(place.getCurrentOpeningHours().getPeriods().get(0).getClose()).getTime().getHours() +
                "h" +
                (Objects.requireNonNull(place.getCurrentOpeningHours().getPeriods().get(0).getClose()).getTime().getMinutes() == 0 ? "00" :
                String.valueOf(Objects.requireNonNull(place.getCurrentOpeningHours().getPeriods().get(0).getClose()).getTime().getMinutes()));


        String editorialSummary = place.getEditorialSummary() != null ?  place.getEditorialSummary() : context.getString(R.string.summary_not_available);

        return new Restaurant(
                place.getId(),
                place.getName(),
                place.getPhoneNumber(),
                Objects.requireNonNull(place.getRating()).floatValue(),
                type.toString(),
                urlPhoto,
                place.getAddress(),
                openingHours,
                editorialSummary,
                urlWebsite,
                Objects.requireNonNull(place.getLatLng()));

    }
    private static Bitmap generateBitmap(VectorDrawable vectorDrawable, boolean isFavorite) {
        int newColor = Color.parseColor("#FFFF5722");
        if(isFavorite){
            newColor = Color.parseColor("#FF4CAF50");
        }
        vectorDrawable.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap generateBitmap(Context context, int drawableId, boolean isFavorite) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return generateBitmap((VectorDrawable) drawable, isFavorite);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
    public static LatLngBounds getCoordinate(double latitude, double longitude, int radius) {
        double latRadius = radius / 111111.0; // approximately 111,111 meters per degree
        double lonRadius = radius / (111111.0 * Math.cos(Math.toRadians(latitude)));

        return new LatLngBounds(
                new LatLng(latitude - latRadius, longitude - lonRadius),
                new LatLng(latitude + latRadius, longitude + lonRadius)
        );
    }
    public static Restaurant createRestaurantFromViewState(RestaurantViewState restaurantViewState) {
        return new Restaurant(restaurantViewState.getRestaurantId(),
                restaurantViewState.getName(),
                restaurantViewState.getPhoneNumber(),
                restaurantViewState.getRating(),
                restaurantViewState.getType(),
                restaurantViewState.getUrlPicture(),
                restaurantViewState.getAddress(),
                restaurantViewState.getOpeningHours(),
                restaurantViewState.getEditorialSummary(),
                restaurantViewState.getUrlWebsite(),
                restaurantViewState.getLatLng());
    }
}