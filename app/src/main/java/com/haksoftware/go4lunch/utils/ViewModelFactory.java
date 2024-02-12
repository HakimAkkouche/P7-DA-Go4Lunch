package com.haksoftware.go4lunch.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.TodayRestaurantRepository;
import com.haksoftware.go4lunch.repository.UserRepository;
import com.haksoftware.go4lunch.ui.detail_restaurant.RestaurantDetailViewModel;
import com.haksoftware.go4lunch.ui.colleagues.ColleaguesViewModel;
import com.haksoftware.go4lunch.ui.restaurant_list.RestaurantListViewViewModel;
import com.haksoftware.go4lunch.ui.map.MapViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory mFactory;
    //private final BuildConfigResolver mBuildConfigResolver = new BuildConfigResolver();
    //private final ToDocRepository mToDocRepository;
    //private final Executor mExecutor = Executors.newFixedThreadPool(4);
   // private final Executor mMainThreadExecutor = new MainThreadExecutor();
    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;
    private TodayRestaurantRepository todayRestaurantRepository;
    private Context context;

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
        todayRestaurantRepository = TodayRestaurantRepository.getInstance();
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(context, restaurantRepository, userRepository, todayRestaurantRepository);
        }
        else if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(context, userRepository);
        }
        else if (modelClass.isAssignableFrom(RestaurantListViewViewModel.class)) {
            return (T) new RestaurantListViewViewModel(userRepository);
        }
        else if (modelClass.isAssignableFrom(ColleaguesViewModel.class)) {
            return (T) new ColleaguesViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}