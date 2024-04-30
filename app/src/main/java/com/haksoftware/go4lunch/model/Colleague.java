package com.haksoftware.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Colleague {
    private String userName;
    private String email;

    private String urlPicture = "";
    @Nullable
    private String lastSelectedRestaurantDate;
    @Nullable
    private Restaurant selectedRestaurant;

    private Boolean wantsNotification;

    public Colleague() { }

    public Colleague( String userName, String email, String urlPicture, boolean wantsNotification) {
        this.userName = userName;
        this.email = email;
        this.urlPicture = urlPicture;
        this.wantsNotification = wantsNotification;
    }

    //region getters/setters

    public String getUserName() {
        return userName;
    }
    public String getUrlPicture() {
        return urlPicture;
    }
    public String getEmail() {
        return email;
    }
    public Boolean getWantsNotification() {
        return wantsNotification;
    }
    @Nullable
    public String getLastSelectedRestaurantDate() {
        return lastSelectedRestaurantDate;
    }
    @Nullable
    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    //endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Colleague)) return false;
        Colleague colleague = (Colleague) o;
        return Objects.equals(userName, colleague.userName) && Objects.equals(email, colleague.email) && Objects.equals(urlPicture, colleague.urlPicture) && Objects.equals(lastSelectedRestaurantDate, colleague.lastSelectedRestaurantDate) && Objects.equals(selectedRestaurant, colleague.selectedRestaurant) && Objects.equals(wantsNotification, colleague.wantsNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, urlPicture, lastSelectedRestaurantDate, selectedRestaurant, wantsNotification);
    }
}
