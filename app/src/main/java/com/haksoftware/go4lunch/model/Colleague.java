package com.haksoftware.go4lunch.model;

import androidx.annotation.Nullable;

public class Colleague {


    private String colleagueId;
    private String userName;
    private String email;
    @Nullable
    private String urlPicture = "";
    private String companyCode;

    private Boolean wantsNotification;

    public Colleague() { }

    public Colleague(String colleagueId, String userName, String email, String urlPicture, @Nullable  Boolean wantsNotification) {
        this.colleagueId = colleagueId;
        this.userName = userName;
        this.email = email;
        this.urlPicture = urlPicture;
        this.wantsNotification = wantsNotification!=null ? wantsNotification : true;
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
    //endregion
}
