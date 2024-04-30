package com.haksoftware.go4lunch.ui.colleagues;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.repository.FirebaseRepository;

import java.util.List;

public class ColleaguesViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepository;

    public ColleaguesViewModel(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    public MutableLiveData<List<Colleague>> getColleagues(){
        return firebaseRepository.getAllColleagues();
    }
}