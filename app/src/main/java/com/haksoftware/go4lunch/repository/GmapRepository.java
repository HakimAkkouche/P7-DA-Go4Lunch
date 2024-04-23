package com.haksoftware.go4lunch.repository;


import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.ResponseGMAP;
import com.haksoftware.go4lunch.utils.PlacesApiHelper;

import retrofit2.Call;

public class GmapRepository {

    private static volatile GmapRepository instance;

    public GmapRepository(){}

    public static GmapRepository getInstance() {
        if(instance == null) {
            instance = new GmapRepository();
        }
        return instance;
    }

    public Call<ResponseGMAP> getNearbyRestaurants(double latitude, double longitude, int radius) {

        return PlacesApiHelper.getNearbyRestaurants(latitude, longitude, radius);
    }/*
    public Call<ResponseDistanceMatrix> getDistance(String destination, String origin, String unit, DistanceMatrixCallback callback) {


        Call<ResponseDistanceMatrix> call = PlacesApiHelper.getDistance(destination, origin, unit);
        //int distance = 0;
        call.enqueue(new Callback<ResponseDistanceMatrix>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDistanceMatrix> call, @NonNull Response<ResponseDistanceMatrix> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        String distance = response.body().getRows().get(0).getElements().get(0).getDistance().getText();
                        //distance.replace(" km", "");
                        callback.onDistanceReceived(distance);
                    }
                } else {
                    callback.onDistanceError("Error : " + response.code() + " - " + response.message());

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDistanceMatrix> call, @NonNull Throwable t) {
                callback.onDistanceError("Error : " + t.getMessage());
            }
        });
        return PlacesApiHelper.getDistance(destination, origin, unit);
    }*/
}
