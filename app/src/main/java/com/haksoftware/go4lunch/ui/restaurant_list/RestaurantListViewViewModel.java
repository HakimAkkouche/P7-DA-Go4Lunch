package com.haksoftware.go4lunch.ui.restaurant_list;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.shareddata.SharedDataMVVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantListViewViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<List<Restaurant>> restaurantMLD;
    private MutableLiveData<List<RestaurantViewState>> restaurantViewStateMLD = new MutableLiveData<>();

    public RestaurantListViewViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restaurantMLD = SharedDataMVVM.getInstance().getRestaurantMutableLiveData();
        this.restaurantViewStateMLD.postValue(getRestaurantViewStateList(Objects.requireNonNull(restaurantMLD.getValue())));
    }

    public MutableLiveData<List<RestaurantViewState>> getRestaurantViewStateMLD() {
        return restaurantViewStateMLD;
    }

    private List<RestaurantViewState> getRestaurantViewStateList(List<Restaurant> restaurantList) {
        List<RestaurantViewState> restaurantViewStateList = new ArrayList<>();
        for (Restaurant restaurant : restaurantList) {
            restaurantViewStateList.add(mapRestaurant(restaurant));
        }
        return restaurantViewStateList;
    }
    private RestaurantViewState mapRestaurant(Restaurant restaurant) {
        return new RestaurantViewState(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                restaurant.getPhoneNumber(),
                restaurant.getRating(),
                restaurant.getType(),
                restaurant.getUrlPicture(),
                restaurant.getAddress(),
                restaurant.getOpeningHours(),
                restaurant.getEditorialSummary(),
                restaurant.getLatLng(),
                10,0);
    }

}