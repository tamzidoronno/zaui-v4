package com.thundashop.core.gotohub.dto;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.PermenantlyDeleteData;
import lombok.Data;

/**
 * @author Naim Murad (naim)
 * @since 6/23/22
 */
@PermenantlyDeleteData
@Data
public class GoToConfiguration extends DataCommon {
    public String authToken = "B?E(H+MbQeThVmYq";
    public String paymentTypeId;
    public int cuttOffHours;
}