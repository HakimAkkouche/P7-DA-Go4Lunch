package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.createRestaurantFromResponseGMAP;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.TodayRestaurantRepository;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.shareddata.SharedDataMVVM;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;
    private TodayRestaurantRepository todayRestaurantRepository;
    private Context context;

    private static final long UPDATE_INTERVAL = 50000; // 5 seconds
    private static final long FASTEST_INTERVAL = 30000; // 3 seconds

    private MutableLiveData<LatLng> currentLocation;
    private MutableLiveData<List<Restaurant>> restaurantMutableLiveData;
    private MutableLiveData<Colleague> colleagueMutableLiveData;

    public MapViewModel(Context context, RestaurantRepository restaurantRepository, UserRepository userRepository, TodayRestaurantRepository todayRestaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.todayRestaurantRepository = todayRestaurantRepository;
        this.restaurantMutableLiveData = SharedDataMVVM.getInstance().getRestaurantMutableLiveData();
        this.currentLocation = SharedDataMVVM.getInstance().getCurrentLocationMutableLiveData();
        this.colleagueMutableLiveData = SharedDataMVVM.getInstance().getColleagueMutableLiveData();
        this.context = context;
    }


    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }
    public void logOut(Context context) {
        userRepository.signOut(context);
    }

    public LiveData<List<Restaurant>> getNearbyRestaurants(double latitude, double longitude, int radius) {
        RestaurantRepository.getInstance().getNearbyRestaurants(latitude, longitude, radius).enqueue(new Callback<ResponseGMAP>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGMAP> call, @NonNull Response<ResponseGMAP> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle the response and add markers for each nearby restaurant
                    List<PlacesItem> results = response.body().getPlaces();
                    List<Restaurant> restaurantList = new ArrayList<>();
                    if (results != null && !results.isEmpty()) {
                        int pos = 0;
                        for (PlacesItem result : results) {

                            LatLng restaurantLatLng = new LatLng(result.getLocation().getLatitude(), result.getLocation().getLongitude());


                            if(result.isDineIn()) {
                                restaurantList.add(createRestaurantFromResponseGMAP(context, result));
                            }
                            pos++;
                        }
                        restaurantMutableLiveData.postValue(restaurantList);
                    }
                    else {
                        Toast.makeText(context, "No nearby restaurants found", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "Error getting nearby restaurants", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGMAP> call, @NonNull Throwable t) {
                // Handle failure
                Toast.makeText(context, "Error getting nearby restaurants: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error", "Error onFailure :getting nearby restaurants: " + t.getMessage());
            }
        });
        return restaurantMutableLiveData;
    }

    public LiveData<List<Restaurant>> getRestaurantMutableLiveData() {
        return restaurantMutableLiveData;
    }
}
