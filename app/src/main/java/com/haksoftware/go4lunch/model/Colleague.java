package com.haksoftware.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Colleague {


    private String colleagueId;
    private String userName;
    private String email;
    @Nullable
    private String urlPicture = "";
    private String companyCode;
    @Nullable
    private String lastSelectedRestaurantDate;
    @Nullable
    private Restaurant selectedRestaurant;

    private Boolean wantsNotification;

    public Colleague() { }

    public Colleague(String colleagueId, String userName, String email, String urlPicture) {
        this.colleagueId = colleagueId;
        this.userName = userName;
        this.email = email;
        this.urlPicture = urlPicture;
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
    public String getCompanyCode() {
        return companyCode;
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
    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public void setUid(String uid) {
        this.colleagueId = uid;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    public void setColleagueId(String colleagueId) {
        this.colleagueId = colleagueId;
    }
    public void setWantsNotification(Boolean wantsNotification) {
        this.wantsNotification = wantsNotification;
    }
    public void setLastSelectedRestaurantDate(@Nullable String lastSelectedRestaurantDate) {
        this.lastSelectedRestaurantDate = lastSelectedRestaurantDate;
    }
    public void setSelectedRestaurant(@Nullable Restaurant  selectedRestaurantId) {
        this.selectedRestaurant = selectedRestaurantId;
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Colleague)) return false;
        Colleague colleague = (Colleague) o;
        return Objects.equals(colleagueId, colleague.colleagueId) && Objects.equals(userName, colleague.userName) && Objects.equals(email, colleague.email) && Objects.equals(urlPicture, colleague.urlPicture) && Objects.equals(companyCode, colleague.companyCode) && Objects.equals(lastSelectedRestaurantDate, colleague.lastSelectedRestaurantDate) && Objects.equals(selectedRestaurant, colleague.selectedRestaurant) && Objects.equals(wantsNotification, colleague.wantsNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colleagueId, userName, email, urlPicture, companyCode, lastSelectedRestaurantDate, selectedRestaurant, wantsNotification);
    }
}
