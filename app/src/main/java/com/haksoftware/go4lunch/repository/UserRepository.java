package com.haksoftware.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String  COLLECTION_NAME = "colleagues";
    private static final String USER_LIKED_RESTAURANT_COLLECTION = "liked_restaurant_list";
    private static final String USER_LIKED_RESTAURANT_ID = "restaurantId";

    private String USER_LAST_DATE_SELECTED = "lastSelectedRestaurantDate";

    private String USER_LAST_REST_SELECTED = "selectedRestaurant";
    private static final String USERNAME_FIELD = "username";
    private MutableLiveData<List<Restaurant>> restaurantLikedListMLD = new MutableLiveData<>();
    private MutableLiveData<List<Colleague>> colleagueList = new MutableLiveData<>();

    private static volatile UserRepository instance;

    private UserRepository() { }

    public static UserRepository getInstance(){
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class){
            if (instance == null) {
                instance = new UserRepository();
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

            Colleague colleagueToCreate = new Colleague(uid, name, email, urlPicture);
            Task<DocumentSnapshot> userData = getColleagueData();
            // If the user already exist in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                this.getColleaguesCollection().document(uid).set(colleagueToCreate);
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

    public MutableLiveData<List<Colleague>> getAllWColleagues() {
        getColleaguesCollection()
                .whereNotEqualTo("colleagueId", getCurrentColleagueID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Colleague> colleagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            colleagues.add(document.toObject(Colleague.class));
                        }
                        colleagueList.setValue(colleagues);
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> colleagueList.postValue(null));
        return colleagueList;

    }
    public void updateSelectedRestaurant(String lastSelectedRestaurantDate, Restaurant selectedRestaurant) {
        getColleaguesCollection().document(getCurrentColleagueID()).update(USER_LAST_DATE_SELECTED, lastSelectedRestaurantDate);
        getColleaguesCollection().document(getCurrentColleagueID()).update(USER_LAST_REST_SELECTED, selectedRestaurant);

    }
    public MutableLiveData<String> getLastSelectedRestaurantDate() {
        String uid = getCurrentColleagueID();
        MutableLiveData<String> lastSelectedRestaurantDateLiveData = new MutableLiveData<>();

        if (uid != null) {
            getColleaguesCollection().document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Colleague colleague = task.getResult().toObject(Colleague.class);
                    if (colleague != null) {
                        lastSelectedRestaurantDateLiveData.setValue(colleague.getLastSelectedRestaurantDate());
                    }
                }
            });
        }

        return lastSelectedRestaurantDateLiveData;
    }

    public MutableLiveData<Restaurant> getSelectedRestaurant() {
        String uid = getCurrentColleagueID();
        MutableLiveData<Restaurant> selectedRestaurantLiveData = new MutableLiveData<>();

        if (uid != null) {
            getColleaguesCollection().document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Colleague colleague = task.getResult().toObject(Colleague.class);
                    if (colleague != null) {
                        selectedRestaurantLiveData.setValue(colleague.getSelectedRestaurant());
                    }
                }
            });
        }

        return selectedRestaurantLiveData;
    }

    public void addLikedRestaurant(Restaurant restaurant) {
        FirebaseUser colleague = getCurrentUser();
        String uid = colleague.getUid();
        this.getColleaguesCollection().document(uid).collection(USER_LIKED_RESTAURANT_COLLECTION).add(restaurant);
    }
    public Task<QuerySnapshot> getLikedRestaurant(String restaurantId) {
        FirebaseUser colleague = getCurrentUser();
        String uid = colleague.getUid();

        return this.getColleaguesCollection()
                .document(uid)
                .collection(USER_LIKED_RESTAURANT_COLLECTION)
                .whereEqualTo("restaurantId", restaurantId)
                .get();
    }
    public void removeLikedRestaurant(Restaurant restaurant) {
        FirebaseUser colleague = getCurrentUser();
        if (colleague != null) {
            String uid = colleague.getUid();
            CollectionReference likedRestaurantsCollection = this.getColleaguesCollection()
                    .document(uid)
                    .collection(USER_LIKED_RESTAURANT_COLLECTION);

            // Recherche du document correspondant au restaurant dans la collection
            likedRestaurantsCollection
                    .whereEqualTo(USER_LIKED_RESTAURANT_ID, restaurant.getRestaurantId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Si le document est trouvé, supprimez-le
                            likedRestaurantsCollection.document(task.getResult().getDocuments().get(0).getId()).delete();
                        } else {
                            // Le document n'a pas été trouvé ou il y a eu une erreur
                            Log.e("RemoveLikedRestaurant", "Document not found or error: " + task.getException());
                        }
                    });
        }
    }
}
