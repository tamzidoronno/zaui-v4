package com.thundashop.zauiactivity.dto;

import com.thundashop.zauiactivity.constant.ZauiConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OctoBookingConfirmRequest {
    private String resellerReference = ZauiConstants.ZAUI_STAY_TAG;
    private OctoConfirmContact contact;
    private Boolean emailConfirmation;
}
