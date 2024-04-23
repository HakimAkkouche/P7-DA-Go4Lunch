package com.haksoftware.go4lunch.ui.restaurant_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantListViewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RestaurantListViewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}