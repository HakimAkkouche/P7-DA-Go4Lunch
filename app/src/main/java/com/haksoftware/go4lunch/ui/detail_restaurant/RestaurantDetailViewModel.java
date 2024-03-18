package com.haksoftware.go4lunch.ui.detail_restaurant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.UserRepository;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel {
    private final UserRepository userRepository;

    public RestaurantDetailViewModel( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<Restaurant> getSelectedRestaurant() {
        MutableLiveData<Restaurant> selectedRestaurantMLD = new MutableLiveData<>();
        userRepository.getSelectedRestaurant(selectedRestaurantMLD::setValue);

        return selectedRestaurantMLD;
    }
    public MutableLiveData<List<Colleague>> getRestaurantColleagues(Restaurant restaurant){
        return userRepository.getColleaguesByRestaurant(restaurant);
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

    public void removeLikedRestaurant(Restaurant restaurant) {
        userRepository.removeLikedRestaurant(restaurant);
    }
    public void updateSelectedRestaurant(String lastSelectedRestaurantDate, Restaurant selectedRestaurant) {
        userRepository.updateSelectedRestaurant(lastSelectedRestaurantDate, selectedRestaurant);
    }
}
