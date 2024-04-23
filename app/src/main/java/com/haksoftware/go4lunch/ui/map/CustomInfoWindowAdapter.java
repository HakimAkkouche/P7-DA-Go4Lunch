package com.haksoftware.go4lunch.ui.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.model.Restaurant;

import java.util.List;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private final List<Restaurant> restaurantList;
    private final View contents;

    @SuppressLint("InflateParams")
    CustomInfoWindowAdapter(Context context, List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        contents = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, contents);
        return contents;
    }

    private void render(Marker marker, View view) {
        int pos = Integer.parseInt(marker.getSnippet());

        String title = restaurantList.get(pos).getName();
        TextView titleUi = view.findViewById(R.id.title);
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

        RatingBar snippetUiType = ( view.findViewById(R.id.info_rating_bar));
        snippetUiType.setRating(restaurantList.get(pos).getRating()*3/5);
        TextView snippetUiDesc = (view.findViewById(R.id.snippet_desc));
        snippetUiDesc.setText(restaurantList.get(pos).getEditorialSummary());
        snippetUiDesc.setOnClickListener(view1 -> {

        });
    }
}
