package com.haksoftware.go4lunch.ui.map;

import android.location.Location;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Root;
import com.haksoftware.go4lunch.repository.ColleaguesRepository;
import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.TodayRestaurantRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import retrofit2.Call;

public class MapViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private ColleaguesRepository colleaguesRepository;
    private TodayRestaurantRepository todayRestaurantRepository;
    private Location currentLocation;


    public MapViewModel(RestaurantRepository restaurantRepository, ColleaguesRepository colleaguesRepository, TodayRestaurantRepository todayRestaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.colleaguesRepository = colleaguesRepository;
        this.todayRestaurantRepository = todayRestaurantRepository;

    }
    public Call<Root> getAllNearbyRestaurants(String url, String location, int radius,
                                              String type, String key) {
        return restaurantRepository.getNearbyRestaurants(url, location, radius, type, key);
    }

    public void addChosenRestaurant(Restaurant restaurantChosen) {
        String colleagueId = colleaguesRepository.getCurrentUser().getUid();
        String userName = colleaguesRepository.getCurrentUser().getDisplayName();
        String urlPicture = colleaguesRepository.getCurrentUser().getPhotoUrl().toString();
        String email = colleaguesRepository.getCurrentUser().getEmail();
        Colleague colleague = new Colleague(colleagueId, userName, email, urlPicture, null);

        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toLocalDate().atStartOfDay();
        todayRestaurantRepository.addTodayRestaurant(date.toString(), restaurantChosen, colleague);
    }

    public FirebaseUser getCurrentUser() {
        return colleaguesRepository.getCurrentUser();
    }
    public void addLikedRestaurant(Restaurant restaurant) {
        colleaguesRepository.addLikedRestaurant(restaurant);
    }
    public void removeLikedRestaurant(Restaurant restaurant) {
        colleaguesRepository.removeLikedRestaurant(restaurant);
    }
    public void cancelTodayRestaurant(Restaurant restaurant, String currentColleagueId) {
        todayRestaurantRepository.cancelTodayRestaurant(restaurant, currentColleagueId);
    }
}