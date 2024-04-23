package com.haksoftware.go4lunch.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haksoftware.go4lunch.repository.GmapRepository;
import com.haksoftware.go4lunch.repository.FirebaseRepository;
import com.haksoftware.go4lunch.ui.map.MapViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory mFactory;
    private final GmapRepository gmapRepository;
    private final FirebaseRepository firebaseRepository;
    private final Application application;

    public static ViewModelFactory getInstance(Application application) {
        if (mFactory == null) {
            synchronized (ViewModelFactory.class) {
                if (mFactory == null) {
                    mFactory = new ViewModelFactory(application);
                }
            }
        }
        return mFactory;
    }

    private ViewModelFactory(Application application) {
        gmapRepository = GmapRepository.getInstance();
        firebaseRepository = FirebaseRepository.getInstance();
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapViewModel.class)) {
            return (T) new MapViewModel(application,firebaseRepository);
        }/*
        else if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(firebaseRepository);
        }
        else if (modelClass.isAssignableFrom(RestaurantListViewModel.class)) {
            return (T) new RestaurantListViewModel(firebaseRepository, gmapRepository);
        }
        else if (modelClass.isAssignableFrom(ColleaguesViewModel.class)) {
            return (T) new ColleaguesViewModel(firebaseRepository);
        }
        else if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(application, firebaseRepository);
        }*/
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}