package com.haksoftware.go4lunch.ui.colleagues;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.repository.UserRepository;

import java.util.List;

public class ColleaguesViewModel extends ViewModel {

    private UserRepository userRepository;

    public ColleaguesViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<List<Colleague>> getColleagues(){
        return userRepository.getAllWColleagues();
    }
}