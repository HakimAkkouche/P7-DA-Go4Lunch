package com.haksoftware.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.haksoftware.go4lunch.repository.ColleaguesRepository;

public class ColleagueManager {

    private static volatile ColleagueManager instance;
    private ColleaguesRepository colleaguesRepository;

    private ColleagueManager() {
        colleaguesRepository = ColleaguesRepository.getInstance();
    }

    public static ColleagueManager getInstance() {
        ColleagueManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ColleaguesRepository.class) {
            if (instance == null) {
                instance = new ColleagueManager();
            }
            return instance;
        }
    }

    public boolean isCurrentColleagueLogged(){
        return (colleaguesRepository.getCurrentUser() != null);
    }

    public FirebaseUser getCurrentColleague() {
        return colleaguesRepository.getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return colleaguesRepository.signOut(context);
    }

    public Task<Void> deleteColleague(Context context){
        return colleaguesRepository.deleteColleague(context);
    }
}
