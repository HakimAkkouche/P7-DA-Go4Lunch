package com.haksoftware.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.TodayRestaurant;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodayRestaurantRepository {
    private static final String COLLECTION_NAME = "today_restaurant";
    private static final String TODAY_REST_COLLEAGUE_ID = "colleagues.colleagueId";
    private static final String TODAY_DATE = "date";
    private static final String TODAY_RESTAURANT_CHOSEN = "restaurant.name";
    private final MutableLiveData<ArrayList<Colleague>> todayColleaguesRestaurantChosen = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTodayRestaurantChosen =new MutableLiveData<>();
    private static volatile TodayRestaurantRepository instance;

    public TodayRestaurantRepository() {
    }
    public static TodayRestaurantRepository getInstance() {
        if (instance == null) {
            instance = new TodayRestaurantRepository();
        }
        return instance;
    }

    public static CollectionReference getTodayRestaurantCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void addTodayRestaurant(String date, Restaurant restaurantChosen, Colleague colleague) {
        TodayRestaurant todayRestaurant = new TodayRestaurant(date, restaurantChosen, colleague);
        getTodayRestaurantCollection().add(todayRestaurant);
    }
    public MutableLiveData<TodayRestaurant> getTodayRestaurant(String idColleague) {
        MutableLiveData<TodayRestaurant> todayRestaurantMutableLiveData = new MutableLiveData<>();
        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toLocalDate().atStartOfDay();

        getTodayRestaurantCollection()
                .whereEqualTo(TODAY_REST_COLLEAGUE_ID, idColleague)
                .whereEqualTo(TODAY_DATE, date.toLocalDate().toString())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(task.getResult().size() != 0) {
                            Log.e("TAG_GET_TODAY_LUNCH",
                                    "getTodayLunch: "+ idColleague +" booked for Lunch this restaurant "
                                            +querySnapshot.toObjects(TodayRestaurant.class).get(0).getRestaurantChosen().getName());
                            todayRestaurantMutableLiveData.postValue(querySnapshot.toObjects(TodayRestaurant.class).get(0));
                        }
                        else {
                            Log.e("TAG_GET_TODAY_LUNCH", "getTodayLunch: "+ idColleague +" hasn't book a restaurant for today lunch !");
                        }
                    }
                    else {
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }
                });

        return todayRestaurantMutableLiveData;
    }
    public static Task<QuerySnapshot> getTodayRestaurantByRestaurant(Restaurant restaurant){
        Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
        return getTodayRestaurantCollection().whereEqualTo(TODAY_RESTAURANT_CHOSEN,restaurant.getName())
                .whereEqualTo(TODAY_DATE,today.toString())
                .get();
    }

    public MutableLiveData<ArrayList<Colleague>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunch(Restaurant restaurant){
        Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
        getTodayRestaurantCollection()
                .whereEqualTo(TODAY_RESTAURANT_CHOSEN,restaurant.getName())
                .whereEqualTo(TODAY_DATE,today.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Colleague> colleagues = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            colleagues.add(document.toObject(TodayRestaurant.class).getColleagues());
                            Log.e("Error", "documents "+document.toObject(TodayRestaurant.class).getColleagues());
                        }

                        todayColleaguesRestaurantChosen.setValue(colleagues);
                    } else {
                        Log.e("Error", "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> todayColleaguesRestaurantChosen.postValue(null));
        return todayColleaguesRestaurantChosen;

    }
    public MutableLiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant,String uid) {
        getTodayRestaurantCollection()
                .whereEqualTo(TODAY_RESTAURANT_CHOSEN, restaurant.getName())
                .whereEqualTo(TODAY_REST_COLLEAGUE_ID,uid)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                isTodayRestaurantChosen.postValue(false);
                                if (task.getResult().size() > 0) {
                                    isTodayRestaurantChosen.postValue(true);
                                }

                            }
                        }
                );
        return isTodayRestaurantChosen;
    }
    public static Task<QuerySnapshot> checkIfCurrentWorkmateChoseThisRestaurantForLunch2(Restaurant restaurant, String uid) {
        return getTodayRestaurantCollection()
                .whereEqualTo(TODAY_RESTAURANT_CHOSEN, restaurant.getName())
                .whereEqualTo(TODAY_REST_COLLEAGUE_ID,uid)
                .get();
    }


    public void cancelTodayRestaurant(Restaurant restaurant, String currentUid) {
        getTodayRestaurantCollection()
                .whereEqualTo(TODAY_RESTAURANT_CHOSEN, restaurant.getName())
                .whereEqualTo(TODAY_REST_COLLEAGUE_ID,currentUid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                            Log.e("delete ", "lunch: "+ document.toObject(TodayRestaurant.class).getRestaurantChosen().getName());
                        }
                    }
                    else {
                        Log.e("Error", "Error delete documents: ", task.getException());
                    }
                });
    }
}
