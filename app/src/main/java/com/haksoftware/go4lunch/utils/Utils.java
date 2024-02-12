package com.haksoftware.go4lunch.utils;

import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo.Result;

public class Utils {
    public static Restaurant createRestaurantFromResultObject(Result result){
        String name = result.getName();
        String phone=result.getName();
        String type2=result.getTypes().get(1);
        String urlPicture=null;
        if(result.getPhotos()!=null) {
            urlPicture = result.getIcon();
        }
        String webSite=null;
        if (result.getWebsite()!=null) {
            webSite=result.getWebsite();
        }
        String adresse=result.getVicinity();
        String hourClosed="";
        System.out.println("url pic :"+urlPicture);
        if (result.getOpeningHours()!=null) {
            if(result.getOpeningHours().getOpenNow())
                hourClosed="OPEN !";
            else
                hourClosed="CLOSED !";
        }
        Float rating =0f;
        if (result.getRating()!=null) {
            rating = result.getRating();
        }
        Restaurant re=new Restaurant(name,name,phone,rating,type2,urlPicture,webSite,adresse,hourClosed);
        return re;

    }
}