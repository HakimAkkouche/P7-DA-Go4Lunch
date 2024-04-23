package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.getRestaurantFromPlacesItem;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.PlacesItem;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.repository.GmapRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends AndroidViewModel {

    private final FirebaseRepository firebaseRepository;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantSelectedMLD = new MutableLiveData<>();


    public MapViewModel(Application application, FirebaseRepository firebaseRepository) {
        super(application);

        this.firebaseRepository = firebaseRepository;
        context = getApplication().getBaseContext();

    }
    public MutableLiveData<List<Restaurant>> getNearbyRestaurants(double latitude, double longitude, int radius) {
        GmapRepository.getInstance().getNearbyRestaurants(latitude, longitude, radius).enqueue(new Callback<ResponseGMAP>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGMAP> call, @NonNull Response<ResponseGMAP> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlacesItem> results = response.body().getPlaces();
                    List<Restaurant> restaurantList = new ArrayList<>();
                    if (results != null && !results.isEmpty()) {
                        for (PlacesItem result : results) {
                            if(result.isDineIn()) {
                                restaurantList.add(getRestaurantFromPlacesItem(context, result));
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
                Log.e("Error", "Error onFailure :getting nearby restaurants: " + t.getMessage());
            }
        });
        return restaurantMutableLiveData;
    }

    public LiveData<List<Restaurant>> getRestaurantMutableLiveData() {
        return restaurantMutableLiveData;
    }
    public FirebaseUser getCurrentUser() {
        return firebaseRepository.getCurrentUser();
    }

}