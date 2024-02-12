package com.haksoftware.go4lunch.utils;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.repository.ColleaguesRepository;
import com.haksoftware.go4lunch.repository.RestaurantRepository;
import com.haksoftware.go4lunch.repository.TodayRestaurantRepository;
import com.haksoftware.go4lunch.ui.colleagues.ColleaguesViewModel;
import com.haksoftware.go4lunch.ui.dashboard.RestaurantListViewViewModel;
import com.haksoftware.go4lunch.ui.map.MapViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory mFactory;
    //private final BuildConfigResolver mBuildConfigResolver = new BuildConfigResolver();
    //private final ToDocRepository mToDocRepository;
    //private final Executor mExecutor = Executors.newFixedThreadPool(4);
   // private final Executor mMainThreadExecutor = new MainThreadExecutor();
    private RestaurantRepository restaurantRepository;
    private ColleaguesRepository colleaguesRepository;
    private TodayRestaurantRepository todayRestaurantRepository;
    private Location currentLocation;

    public static ViewModelFactory getInstance() {
        if (mFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (mFactory == null) {
                    mFactory = new ViewModelFactory();
                }
            }
        }
        return mFactory;
    }

    private ViewModelFactory() {
        restaurantRepository = RestaurantRepository.getInstance();
        colleaguesRepository = ColleaguesRepository.getInstance();
        todayRestaurantRepository = TodayRestaurantRepository.getInstance();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(restaurantRepository,colleaguesRepository, todayRestaurantRepository);
        } else if (modelClass.isAssignableFrom(RestaurantListViewViewModel.class)) {
            return (T) new RestaurantListViewViewModel();
        }
        else if (modelClass.isAssignableFrom(ColleaguesViewModel.class)) {
            return (T) new ColleaguesViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}