package com.haksoftware.go4lunch.utils;

import com.haksoftware.go4lunch.model.Restaurant;

import java.util.Comparator;
import java.util.Objects;

public class RestaurantDistanceComparator implements Comparator<Restaurant> {
    @Override
    public int compare(Restaurant r1, Restaurant
            r2) {
        String distanceR1 = Objects.requireNonNull(String.valueOf(r1.getDistance())).replace(" km", "");
        String distanceR2 = Objects.requireNonNull(r2.getDistance()).replace(" km", "");
        distanceR1 = distanceR1.replace(" m", "");
        distanceR2 = distanceR2.replace(" m", "");
        return Double.compare(Double.parseDouble(distanceR1),
                Double.parseDouble(distanceR2));
    }
}
