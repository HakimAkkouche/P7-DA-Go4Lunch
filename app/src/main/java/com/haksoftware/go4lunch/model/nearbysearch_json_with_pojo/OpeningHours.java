package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class OpeningHours{
    @SerializedName("open_now")
    private Boolean openNow;
    @SerializedName("additional_properties")
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public OpeningHours(Boolean openNow, Map<String, Object> additionalProperties) {
        this.openNow = openNow;
        this.additionalProperties = additionalProperties;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
