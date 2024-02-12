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
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;

public class Utils {
    public static Restaurant createRestaurantFromResponseGMAP(Context context, PlacesItem result){
        String restaurantId = result.getId();
        String name = result.getDisplayName().getText();
        String phone = result.getNationalPhoneNumber();
        String type = "";
        for (String s :
                result.getTypes()) {
            type += s + ", ";

        }
        String urlPhoto = "";
        if(result.getPhotos() != null) {
            urlPhoto = result.getPhotos().get(0).getName();
        }

        String urlWebsite = "";
        if(result.getWebsiteUri() != null) {
            urlWebsite = result.getWebsiteUri();
        }

        String address = result.getFormattedAddress();
        String openingHours = ""/*result.getCurrentOpeningHours().getOpeningHoursToday()*/;
        String editorialSummary = result.getEditorialSummary() != null ?  result.getEditorialSummary().getText() : context.getString(R.string.summary_not_available);

        LatLng restaurantLatLng = new LatLng(result.getLocation().getLatitude(), result.getLocation().getLongitude());

        float rating = result.getRating();


        Restaurant re = new Restaurant(restaurantId, name, phone, rating, type, urlPhoto, address, openingHours, editorialSummary, urlWebsite, restaurantLatLng);
        return re;

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
        Log.e("TAG", "getBitmap: 1");
        return bitmap;
    }

    public static Bitmap generateBitmap(Context context, int drawableId, boolean isFavorite) {
        Log.e("TAG", "getBitmap: 2");
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return generateBitmap((VectorDrawable) drawable, isFavorite);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}