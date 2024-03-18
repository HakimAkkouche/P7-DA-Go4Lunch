package com.haksoftware.go4lunch.utils;

import com.haksoftware.go4lunch.ui.restaurant_list.RestaurantViewState;

import java.util.Comparator;
import java.util.Objects;

public class RestaurantDistanceComparator implements Comparator<RestaurantViewState> {
    @Override
    public int compare(RestaurantViewState r1, RestaurantViewState r2) {
        String distanceR1 = Objects.requireNonNull(r1.getDistance().getValue()).replace(" km", "");
        String distanceR2 = Objects.requireNonNull(r2.getDistance().getValue()).replace(" km", "");
        distanceR1 = distanceR1.replace(" m", "");
        distanceR2 = distanceR2.replace(" m", "");
        return Double.compare(Double.parseDouble(distanceR1),
                Double.parseDouble(distanceR2));
    }
}
