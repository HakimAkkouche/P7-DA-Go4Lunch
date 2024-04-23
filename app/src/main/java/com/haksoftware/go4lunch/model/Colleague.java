package com.haksoftware.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Colleague {
    private String colleagueId;
    private String userName;
    private String email;

    private String urlPicture = "";
    @Nullable
    private String lastSelectedRestaurantDate;
    @Nullable
    private String selectedRestaurant;

    private Boolean wantsNotification;

    public Colleague() { }

    public Colleague(String colleagueId, String userName, String email, String urlPicture, boolean wantsNotification) {
        this.colleagueId = colleagueId;
        this.userName = userName;
        this.email = email;
        this.urlPicture = urlPicture;
        this.wantsNotification = wantsNotification;
    }

    //region getters/setters
    public String getUid() {
        return colleagueId;
    }

    public String getUserName() {
        return userName;
    }
    public String getUrlPicture() {
        return urlPicture;
    }
    public String getColleagueId() {
        return colleagueId;
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
    public String getSelectedRestaurant() {
        return selectedRestaurant;
    }

    //endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Colleague)) return false;
        Colleague colleague = (Colleague) o;
        return Objects.equals(colleagueId, colleague.colleagueId) && Objects.equals(userName, colleague.userName) && Objects.equals(email, colleague.email) && Objects.equals(urlPicture, colleague.urlPicture) && Objects.equals(lastSelectedRestaurantDate, colleague.lastSelectedRestaurantDate) && Objects.equals(selectedRestaurant, colleague.selectedRestaurant) && Objects.equals(wantsNotification, colleague.wantsNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colleagueId, userName, email, urlPicture, lastSelectedRestaurantDate, selectedRestaurant, wantsNotification);
    }
}
