package com.thundashop.core.gotohub.dto;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.gotohub.constant.GotoConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Naim Murad (naim)
 * @since 6/23/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GoToConfiguration extends DataCommon {
    public String authToken;
    public String paymentTypeId;
    public int cuttOffHours;
    private String email = "";
    private int unpaidBookingExpirationTime = GotoConstants.DEFAULT_AUTO_DELETION_TIME_FOR_GOTO_BOOKING_IN_MINUTE;
}