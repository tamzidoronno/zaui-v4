
package com.thundashop.core.gotohub.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GotoBookerMobile {

    @SerializedName("areaCode")
    @Expose
    private String areaCode;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
}
