package com.haksoftware.go4lunch.ui.detail_restaurant;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.shareddata.SharedDataMVVM;


public class RestaurantDetailViewModel extends ViewModel {
    private UserRepository userRepository;
    private Context context;
    private MutableLiveData<LatLng> currentLocation;
    private MutableLiveData<Boolean> isLikedRestaurant = new MutableLiveData<>();

    public RestaurantDetailViewModel(Context context, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentLocation = SharedDataMVVM.getInstance().getCurrentLocationMutableLiveData();
        this.context = context;
    }

    public MutableLiveData<String> getLastSelectedRestaurantDate() {
        return userRepository.getLastSelectedRestaurantDate();
    }

    public MutableLiveData<Restaurant> getSelectedRestaurant() {
        return userRepository.getSelectedRestaurant();
    }

    public void addLikedRestaurant(Restaurant restaurant) {
        userRepository.addLikedRestaurant(restaurant);
    }
    public LiveData<DocumentSnapshot> getLikedRestaurant(String restaurantId) {
        MutableLiveData<DocumentSnapshot> result = new MutableLiveData<>();

        userRepository.getLikedRestaurant(restaurantId)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        result.postValue(task.getResult().getDocuments().get(0));
                    } else {
                        result.postValue(null);
                    }
                });
        return result;
    }
    public MutableLiveData<Boolean> getIsLikedRestaurant() {
        return isLikedRestaurant;
    }

    public void removeLikedRestaurant(Restaurant restaurant) {
        userRepository.removeLikedRestaurant(restaurant);
    }
    public void updateSelectedRestaurant(String lastSelectedRestaurantDate, Restaurant selectedRestaurant) {
        userRepository.updateSelectedRestaurant(lastSelectedRestaurantDate, selectedRestaurant);
    }

}
