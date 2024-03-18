package com.haksoftware.go4lunch.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.detail_restaurant.RestaurantDetailViewModel;
import com.haksoftware.go4lunch.ui.colleagues.ColleaguesViewModel;
import com.haksoftware.go4lunch.ui.restaurant_list.RestaurantListViewViewModel;
import com.haksoftware.go4lunch.ui.map.MapViewModel;
import com.haksoftware.go4lunch.ui.settings.SettingsViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory mFactory;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final Context context;

    public static ViewModelFactory getInstance(Context context) {
        if (mFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (mFactory == null) {
                    mFactory = new ViewModelFactory(context);
                }
            }
        }
        return mFactory;
    }

    private ViewModelFactory(Context context) {
        restaurantRepository = RestaurantRepository.getInstance();
        userRepository = UserRepository.getInstance();
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(context, userRepository);
        }
        else if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(userRepository);
        }
        else if (modelClass.isAssignableFrom(RestaurantListViewViewModel.class)) {
            return (T) new RestaurantListViewViewModel(userRepository, restaurantRepository);
        }
        else if (modelClass.isAssignableFrom(ColleaguesViewModel.class)) {
            return (T) new ColleaguesViewModel(userRepository);
        }
        else if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(context, userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}