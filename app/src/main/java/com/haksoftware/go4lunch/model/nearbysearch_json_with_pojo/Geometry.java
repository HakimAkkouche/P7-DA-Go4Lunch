package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;


import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Geometry{
    @SerializedName("locationJson")
    private LocationJson locationJson;
    @SerializedName("viewport")
    private Viewport viewport;
    @SerializedName("additional_properties")
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Geometry(LocationJson locationJson, Viewport viewport, Map<String, Object> additionalProperties) {
        this.locationJson = locationJson;
        this.viewport = viewport;
        this.additionalProperties = additionalProperties;
    }

    public void setLocation(LocationJson locationJson) {
        this.locationJson = locationJson;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public LocationJson getLocation() {
        return locationJson;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
}
