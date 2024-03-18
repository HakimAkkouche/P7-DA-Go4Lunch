package com.haksoftware.go4lunch.ui.restaurant_list;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.shareddata.SharedDataMVVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantListViewViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MutableLiveData<Location> currentLocation;
    private final MutableLiveData<List<RestaurantViewState>> restaurantViewStateMLD = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDistanceLoaded = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCountLoaded = new MutableLiveData<>();

    public RestaurantListViewViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        MutableLiveData<List<Restaurant>> restaurantMLD = SharedDataMVVM.getInstance().getRestaurantMutableLiveData();
        this.currentLocation = SharedDataMVVM.getInstance().getCurrentLocationMutableLiveData();
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

        String destination = restaurant.getLatitude() + "," + restaurant.getLongitude();
        String origin = Objects.requireNonNull(currentLocation.getValue()).getLatitude() + "," + currentLocation.getValue().getLongitude();

        RestaurantViewState restaurantViewState = new RestaurantViewState(
                restaurant.getRestaurantId(),
                restaurant.getName(),
                restaurant.getPhoneNumber(),
                restaurant.getRating(),
                restaurant.getType(),
                restaurant.getUrlPicture(),
                restaurant.getAddress(),
                restaurant.getOpeningHours(),
                restaurant.getEditorialSummary(),
                restaurant.getUrlWebsite(),
                restaurant.getLatLng(),
                 "0");

        restaurantRepository.getDistance(destination, origin, "metric", new DistanceMatrixCallback() {
            @Override
            public void onDistanceReceived(String  distance) {
                restaurantViewState.setDistance(distance);
                isDistanceLoaded.setValue(true);
            }

            @Override
            public void onDistanceError(String errorMessage) {
                Log.e("DistanceMatrixError", errorMessage);
            }
        });
        userRepository.getColleaguesCount(restaurant, new ColleagueRestaurantCallback() {
            @Override
            public void onColleaguesReceived(int participants) {
                restaurantViewState.setColleaguesCount("(" + participants + ")");
                isCountLoaded.setValue(true);
            }

            @Override
            public void onColleaguesError(String errorMessage) {
                Log.e("Colleagues count", errorMessage);
            }
        });
        return restaurantViewState;
    }

    public MutableLiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public MutableLiveData<Boolean> getIsDistanceLoaded() {
        return isDistanceLoaded;
    }

    public MutableLiveData<Boolean> getIsCountLoaded() {
        return isCountLoaded;
    }
}