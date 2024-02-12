package com.haksoftware.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;

public class ColleaguesRepository {

    private static final String  COLLECTION_NAME = "colleagues";
    private static final String COLLEAGUE_LIKED_RESTAURANT_COLLECTION = "liked_restaurant_list";
    private static final String COLLEAGUE_LIKED_RESTAURANT = "liked_restaurant";
    private static final String USERNAME_FIELD = "username";

    private static volatile ColleaguesRepository instance;

    private ColleaguesRepository() { }

    public static ColleaguesRepository getInstance(){
        ColleaguesRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ColleaguesRepository.class){
            if (instance == null) {
                instance = new ColleaguesRepository();
            }
            return instance;
        }
    }
    public CollectionReference getColleaguesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
    public void createColleague() {
        FirebaseUser user = getCurrentUser();
        if(user != null) {
            String uid = user.getUid();
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String name = user.getDisplayName();
            String email = user.getEmail();

            Colleague userToCreate = new Colleague(uid, name, email, urlPicture, null);
            Task<DocumentSnapshot> userData = getColleagueData();
            // If the user already exist in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getColleaguesCollection().document(uid).set(userToCreate);
            });
        }
    }
    public String getCurrentColleagueID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
    // Get Colleague Data from Firestore
    public Task<DocumentSnapshot> getColleagueData(){
        String uid = this.getCurrentColleagueID();
        if(uid != null){
            return this.getColleaguesCollection().document(uid).get();
        }else{
            return null;
        }
    }
    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteColleague(Context context){
        return AuthUI.getInstance().delete(context);
    }
    public void addLikedRestaurant(Restaurant restaurant) {
        FirebaseUser workmates = getCurrentUser();
        String uid = workmates.getUid();
        this.getColleaguesCollection().document(uid).collection(COLLEAGUE_LIKED_RESTAURANT_COLLECTION).add(restaurant);
    }
    public void removeLikedRestaurant(Restaurant restaurant) {
        FirebaseUser colleague = getCurrentUser();
        String colleagueId = colleague.getUid();

        this.getColleaguesCollection().document(colleagueId)
                .collection(COLLEAGUE_LIKED_RESTAURANT_COLLECTION)
                .whereEqualTo(COLLEAGUE_LIKED_RESTAURANT, restaurant.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();

                        }
                    }
                    else {
                        Log.d("Error", "Error delete documents : ", task.getException());
                    }
                });
    }
}
