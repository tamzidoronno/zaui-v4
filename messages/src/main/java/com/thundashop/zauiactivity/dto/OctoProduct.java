package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OctoProduct {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("internalName")
    @Expose
    private String internalName;
    @SerializedName("options")
    @Expose
    private List<ActivityOption> options;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("coverImage")
    @Expose
    private String coverImage;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;
    @SerializedName("primaryDescription")
    @Expose
    private String primaryDescription;
}
