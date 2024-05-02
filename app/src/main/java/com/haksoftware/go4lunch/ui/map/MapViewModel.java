package com.haksoftware.go4lunch.ui.map;

import static com.haksoftware.go4lunch.utils.Utils.getRestaurantFromPlacesItem;

import android.app.Application;
import android.content.Context;
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
import com.haksoftware.go4lunch.ui.restaurant_list.ColleagueRestaurantCallback;
import com.haksoftware.go4lunch.ui.restaurant_list.DistanceMatrixCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewModel extends AndroidViewModel {

    private final FirebaseRepository firebaseRepository;
    private final GmapRepository gmapRepository;
    private final Context context;
    private final MutableLiveData<List<Restaurant>> restaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantSelectedMLD = new MutableLiveData<>();
    private int expectedColleaguesCount = 0;
    private int receivedColleaguesCount = 0;

    public MapViewModel(Application application, FirebaseRepository firebaseRepository, GmapRepository gmapRepository) {
        super(application);

        this.firebaseRepository = firebaseRepository;
        this.gmapRepository = gmapRepository;
        context = getApplication().getApplicationContext();

    }
    public MutableLiveData<List<Restaurant>> getNearbyRestaurants(double latitude, double longitude, int radius, ColleaguesCountLoadedCallback callback) {
        String origin = latitude + "," + longitude;

        gmapRepository.getNearbyRestaurants(latitude, longitude, radius).enqueue(new Callback<ResponseGMAP>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGMAP> call, @NonNull Response<ResponseGMAP> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlacesItem> results = response.body().getPlaces();
                    List<Restaurant> restaurantList = new ArrayList<>();
                    if (results != null && !results.isEmpty()) {
                        int i = 0;
                        for (PlacesItem result : results) {
                            Restaurant restaurant = getRestaurantFromPlacesItem(result);

                            String destination = restaurant.getLatitude() + "," + restaurant.getLongitude();

                            setDistance(i, destination, origin);
                            setColleaguesCount(i, restaurant, callback);
                            restaurantList.add(restaurant);
                            i++;
                        }
                        expectedColleaguesCount = restaurantList.size();
                        restaurantMutableLiveData.setValue(restaurantList);
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

    public MutableLiveData<Restaurant> getTodaySelectedRestaurant() {
        MutableLiveData<Restaurant> restaurantMLD = firebaseRepository.getSelectedRestaurant(restaurantSelectedMLD::setValue);
        if (restaurantMLD != null){
            return restaurantMLD;
        }
        return restaurantSelectedMLD;
    }

    private void setDistance(int index, String destination, String origin) {
        gmapRepository.getDistance(destination, origin, "metric", new DistanceMatrixCallback() {
            @Override
            public void onDistanceReceived(String  distance) {
                Objects.requireNonNull(restaurantMutableLiveData.getValue()).get(index).setDistance(distance);
            }

            @Override
            public void onDistanceError(String errorMessage) {
                Log.e("DistanceMatrixError", errorMessage);
            }
        });
    }
    private void setColleaguesCount(int index, Restaurant restaurant, ColleaguesCountLoadedCallback callback) {
        firebaseRepository.getColleaguesCount(restaurant, new ColleagueRestaurantCallback() {
            @Override
            public void onColleaguesReceived(int participants) {
                Objects.requireNonNull(restaurantMutableLiveData.getValue()).get(index).setColleaguesCount(participants);
                receivedColleaguesCount++;
                if (receivedColleaguesCount == expectedColleaguesCount) {
                    callback.colleaguesCountLoaded(restaurantMutableLiveData.getValue());
                }
            }

            @Override
            public void onColleaguesError(String errorMessage) {
                Log.e("Colleagues count", errorMessage);
                receivedColleaguesCount++;
                if (receivedColleaguesCount == expectedColleaguesCount) {
                    callback.colleaguesCountLoaded(restaurantMutableLiveData.getValue());
                }
            }
        });
    }
}