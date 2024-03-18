package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.createRestaurantFromResponseGMAP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.shareddata.SharedDataMVVM;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel {

    private final UserRepository userRepository;
    @SuppressLint("StaticFieldLeak")
    private final Context context;

    private final MutableLiveData<Location> currentLocation;
    private final MutableLiveData<List<Restaurant>> restaurantMutableLiveData;
    private final MutableLiveData<Restaurant> restaurantSelectedMLD = new MutableLiveData<>();

    public MapViewModel(Context context, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restaurantMutableLiveData = SharedDataMVVM.getInstance().getRestaurantMutableLiveData();
        this.currentLocation = SharedDataMVVM.getInstance().getCurrentLocationMutableLiveData();
        this.context = context;
    }


    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public LiveData<List<Restaurant>> getNearbyRestaurants(double latitude, double longitude, int radius) {
        RestaurantRepository.getInstance().getNearbyRestaurants(latitude, longitude, radius).enqueue(new Callback<ResponseGMAP>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGMAP> call, @NonNull Response<ResponseGMAP> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlacesItem> results = response.body().getPlaces();
                    List<Restaurant> restaurantList = new ArrayList<>();
                    if (results != null && !results.isEmpty()) {
                        for (PlacesItem result : results) {
                            if(result.isDineIn()) {
                                restaurantList.add(createRestaurantFromResponseGMAP(context, result));
                            }
                        }
                        restaurantMutableLiveData.postValue(restaurantList);
                    }
                    else {
                        if(response.errorBody() != null) {
                            Log.e("error:", response.errorBody().toString());
                            Toast.makeText(context, "No nearby restaurants found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    if(response.errorBody() != null) {
                        Log.e("error:", response.errorBody().toString());
                        Toast.makeText(context, "Error getting nearby restaurants", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGMAP> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error getting nearby restaurants: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error onFailure :getting nearby restaurants: " + t.getMessage());
            }
        });
        return restaurantMutableLiveData;
    }
    public MutableLiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<List<Restaurant>> getRestaurantMutableLiveData() {
        return restaurantMutableLiveData;
    }
    public MutableLiveData<Restaurant> getTodaySelectedRestaurant() {
        userRepository.getSelectedRestaurant(restaurantSelectedMLD::setValue);

        return restaurantSelectedMLD;
    }
}
