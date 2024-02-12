package com.haksoftware.go4lunch.retrofit;

import java.util.List;
public class NearbySearchRequest {

    private List<String> includedTypes;
    private String languageCode;
    private int maxResultCount;
    private LocationRestriction locationRestriction;

    public NearbySearchRequest(List<String> includedTypes, String languageCode, int maxResultCount, LocationRestriction locationRestriction) {
        this.includedTypes = includedTypes;
        this.languageCode = languageCode;
        this.maxResultCount = maxResultCount;
        this.locationRestriction = locationRestriction;
    }

}



