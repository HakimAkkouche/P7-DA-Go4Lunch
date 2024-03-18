package com.haksoftware.go4lunch.ui.shareddata;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.haksoftware.go4lunch.model.Restaurant;

import java.util.List;

public class SharedDataMVVM {
    private final MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocationMutableLiveData = new MutableLiveData<>();

    private static SharedDataMVVM instance;
    public static synchronized SharedDataMVVM getInstance(){
        if(instance == null) {
            instance = new SharedDataMVVM();
        }
        return instance;
    }

    public MutableLiveData<List<Restaurant>> getRestaurantMutableLiveData() {
        return restaurantMutableLiveData;
    }
    public MutableLiveData<Location> getCurrentLocationMutableLiveData() {
        return currentLocationMutableLiveData;
    }
}
