package com.haksoftware.go4lunch.model.nearbysearch_json_with_pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Photo{
    @SerializedName("height")
    private Integer height;
    @SerializedName("html_attributions")
    private List<String> htmlAttributions = null;
    @SerializedName("photo_reference")
    private String photoReference;
    @SerializedName("width")
    private Integer width;
    @SerializedName("additional_properties")
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    public Integer getHeight() {
        return height;
    }

    public Photo(Integer height, List<String> htmlAttributions, String photoReference, Integer width, Map<String, Object> additionalProperties) {
        this.height = height;
        this.htmlAttributions = htmlAttributions;
        this.photoReference = photoReference;
        this.width = width;
        this.additionalProperties = additionalProperties;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public Integer getWidth() {
        return width;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
}
