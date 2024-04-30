package com.haksoftware.go4lunch.ui.restaurant_list;

public interface ColleagueRestaurantCallback {
    void onColleaguesReceived(int participants);
    void onColleaguesError(String errorMessage);
}
