package com.haksoftware.go4lunch.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.LikedRestaurant;
import com.haksoftware.go4lunch.ui.detail_restaurant.LikedRestaurantCallback;
import com.haksoftware.go4lunch.ui.restaurant_list.ColleagueRestaurantCallback;
import com.haksoftware.go4lunch.utils.RestaurantSelectedCallback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseRepository {

    private static final String  COLLECTION_NAME = "colleagues";
    private static final String USER_LIKED_RESTAURANT_COLLECTION = "liked_restaurant_list";
    private static final String USER_LIKED_RESTAURANT_ID = "restaurantId";
    private final MutableLiveData<List<Colleague>> colleagueList = new MutableLiveData<>();
    private static volatile FirebaseRepository instance;
    private FirebaseRepository() { }

    public static FirebaseRepository getInstance(){
        FirebaseRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(FirebaseRepository.class){
            if (instance == null) {
                instance = new FirebaseRepository();
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

            Colleague colleagueToCreate = new Colleague( name, email, urlPicture, false);
            Task<DocumentSnapshot> userData = getColleagueData();

            userData.addOnSuccessListener(documentSnapshot ->
                this.getColleaguesCollection().document(uid).set(colleagueToCreate));
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

    public void signOut(Context context){
        AuthUI.getInstance().signOut(context);
    }

    public MutableLiveData<List<Colleague>> getAllColleagues() {
        getColleaguesCollection()
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
    public MutableLiveData<List<Colleague>> getColleaguesByRestaurant(String restaurantId) {

        getColleaguesCollection()
                .whereEqualTo("lastSelectedRestaurantDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Colleague> colleagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Colleague colleague = document.toObject(Colleague.class);
                            if(Objects.requireNonNull(colleague.getSelectedRestaurant()).getRestaurantId().equals(restaurantId)) {
                                colleagues.add(colleague);
                            }
                        }
                        colleagueList.setValue(colleagues);
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> colleagueList.postValue(null));
        return colleagueList;

    }
    public void getColleaguesCount(Restaurant restaurant, ColleagueRestaurantCallback callback) {

        getColleaguesCollection()
                .whereEqualTo("lastSelectedRestaurantDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Colleague> colleagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Colleague colleague = document.toObject(Colleague.class);
                            if (colleague.getSelectedRestaurant() != null) {
                                if (colleague.getSelectedRestaurant().getRestaurantId().equals(restaurant.getRestaurantId())) {
                                    colleagues.add(colleague);
                                }
                            }
                        }
                        colleagueList.setValue(colleagues);
                        callback.onColleaguesReceived(colleagues.size());
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                        callback.onColleaguesError("Error getting documents: " + task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Error", "Error getting colleagues", e);
                    callback.onColleaguesError("Error getting colleagues: " + e.getMessage());
                });
    }
    public void updateSelectedRestaurant(String lastSelectedRestaurantDate, Restaurant selectedRestaurant) {
        getColleaguesCollection().document(getCurrentColleagueID()).update("lastSelectedRestaurantDate", lastSelectedRestaurantDate);
        getColleaguesCollection().document(getCurrentColleagueID()).update("selectedRestaurant", selectedRestaurant);

    }
    public MutableLiveData<Restaurant> getSelectedRestaurant(RestaurantSelectedCallback callback) {
        String uid = getCurrentColleagueID();
        MutableLiveData<Restaurant> selectedRestaurantLiveData = new MutableLiveData<>();

        if (uid != null) {
            getColleaguesCollection().document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Colleague colleague = task.getResult().toObject(Colleague.class);
                    if (colleague != null) {
                        if(colleague.getLastSelectedRestaurantDate() != null) {
                            if (colleague.getLastSelectedRestaurantDate()
                                    .equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))) {
                                selectedRestaurantLiveData.setValue(colleague.getSelectedRestaurant());
                            }
                        }
                        else {
                            selectedRestaurantLiveData.setValue(null);
                        }
                        callback.onRestaurantSelected(selectedRestaurantLiveData.getValue());
                    }
                }
            });
        }
        return selectedRestaurantLiveData;
    }

    public void addLikedRestaurant(String restaurantId) {
        FirebaseUser colleague = getCurrentUser();
        this.getColleaguesCollection().document(colleague.getUid()).collection(USER_LIKED_RESTAURANT_COLLECTION).add(new LikedRestaurant(restaurantId));
    }
    public void getLikedRestaurant(String restaurantId, LikedRestaurantCallback callback) {
        FirebaseUser colleague = getCurrentUser();
        if(colleague != null) {
            this.getColleaguesCollection()
                    .document(colleague.getUid())
                    .collection(USER_LIKED_RESTAURANT_COLLECTION)
                    .whereEqualTo("restaurantId", restaurantId)
                    .get().addOnCompleteListener(task -> callback.onLikedRestaurantReceived(task.isSuccessful() && !task.getResult().isEmpty()));
        }
    }
    public void removeLikedRestaurant(String restaurantID) {
        FirebaseUser colleague = getCurrentUser();
        if (colleague != null) {
            String uid = colleague.getUid();
            CollectionReference likedRestaurantsCollection = this.getColleaguesCollection()
                    .document(uid)
                    .collection(USER_LIKED_RESTAURANT_COLLECTION);

            // Recherche du document correspondant au restaurant dans la collection
            likedRestaurantsCollection
                    .whereEqualTo(USER_LIKED_RESTAURANT_ID, restaurantID)
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
    public UploadTask uploadImage(Uri imageUri) {

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(getCurrentColleagueID() + "_profile.jpg");
        return imageRef.putFile(imageUri);
    }

    public void setNewUrlPicture(Uri profileImageUri) {
        FirebaseUser user = getCurrentUser();
        if(user != null){

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUri.toString()))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Info", "User profile updated.");
                        }
                    });
        }
    }
    public MutableLiveData<Boolean> getWantsNotification(){
        String uid = getCurrentColleagueID();
        MutableLiveData<Boolean> wantsNotificationMLD = new MutableLiveData<>();

        if (uid != null) {
            getColleaguesCollection().document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Colleague colleague = task.getResult().toObject(Colleague.class);
                    if (colleague != null) {
                        wantsNotificationMLD.setValue(colleague.getWantsNotification());
                    }
                }
            });
        }
        return wantsNotificationMLD;
    }
    public void setWantsNotification(boolean wantsNotification) {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            getColleaguesCollection().document(user.getUid()).update("wantsNotification", wantsNotification);
        }
    }
}
