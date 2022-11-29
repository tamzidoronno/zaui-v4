package com.thundashop.zauiactivity.dto;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thundashop.core.common.DataCommon;
import lombok.Data;

import javax.jdo.annotations.Serialized;

@Data
public class OctoProduct {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
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


