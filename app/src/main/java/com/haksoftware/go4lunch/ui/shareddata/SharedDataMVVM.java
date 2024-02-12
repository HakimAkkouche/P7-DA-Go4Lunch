package com.haksoftware.go4lunch.ui.shareddata;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;

import java.util.List;

public class SharedDataMVVM {
    private MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> currentLocationMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Colleague> colleagueMutableLiveData = new MutableLiveData<Colleague>();

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
    public MutableLiveData<LatLng> getCurrentLocationMutableLiveData() {
        return currentLocationMutableLiveData;
    }

    public MutableLiveData<Colleague> getColleagueMutableLiveData() {
        return colleagueMutableLiveData;
    }

}
