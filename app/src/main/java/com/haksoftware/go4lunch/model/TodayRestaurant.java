package com.haksoftware.go4lunch.model;

public class TodayRestaurant {
    private String date;
    private Restaurant restaurantChosen;
    private Colleague colleague;

    public TodayRestaurant(String date, Restaurant restaurantChosen, Colleague colleague) {
        this.date = date;
        this.restaurantChosen = restaurantChosen;
        this.colleague = colleague;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Restaurant getRestaurantChosen() {
        return restaurantChosen;
    }

    public void setRestaurantChosen(Restaurant restaurantChosen) {
        this.restaurantChosen = restaurantChosen;
    }

    public Colleague getColleagues() {
        return colleague;
    }

    public void setColleagues(Colleague colleague) {
        this.colleague = colleague;
    }
}
