package com.haksoftware.go4lunch.ui.colleagues;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ColleaguesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ColleaguesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}