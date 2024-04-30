package com.haksoftware.go4lunch.ui.restaurant_list;

public interface DistanceMatrixCallback {
    void onDistanceReceived(String distance);
    void onDistanceError(String errorMessage);
}
