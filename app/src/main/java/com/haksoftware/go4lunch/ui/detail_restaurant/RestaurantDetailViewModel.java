package com.haksoftware.go4lunch.ui.detail_restaurant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel {

    private LikedRestaurantCallback likedRestaurantCallback;
    private final FirebaseRepository firebaseRepository;

    public RestaurantDetailViewModel( FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    public MutableLiveData<Restaurant> getSelectedRestaurant() {
        MutableLiveData<Restaurant> selectedRestaurantMLD = new MutableLiveData<>();
        firebaseRepository.getSelectedRestaurant(selectedRestaurantMLD::setValue);

        return selectedRestaurantMLD;
    }
    public MutableLiveData<List<Colleague>> getRestaurantColleagues(Restaurant restaurant){
        return firebaseRepository.getColleaguesByRestaurant(restaurant.getRestaurantId());
    }
    public void setLikedRestaurantCallback(LikedRestaurantCallback callback) {
        this.likedRestaurantCallback = callback;
    }

    public MutableLiveData<Boolean> getLikedRestaurant(String restaurantId) {

        MutableLiveData<Boolean> likedRestaurant = new MutableLiveData<>();
        firebaseRepository.getLikedRestaurant(restaurantId, isLikedRestaurant -> {
            if (likedRestaurantCallback != null) {
                likedRestaurant.setValue(isLikedRestaurant);
                likedRestaurantCallback.onLikedRestaurantReceived(isLikedRestaurant);
            }
        });
        return likedRestaurant;
    }
    public void addLikedRestaurant(String restaurant) {
        firebaseRepository.addLikedRestaurant(restaurant);
    }

    public void removeLikedRestaurant(String restaurantId) {
        firebaseRepository.removeLikedRestaurant(restaurantId);
    }
    public void updateSelectedRestaurant(String lastSelectedRestaurantDate, Restaurant selectedRestaurant) {
        firebaseRepository.updateSelectedRestaurant(lastSelectedRestaurantDate, selectedRestaurant);
    }
}
