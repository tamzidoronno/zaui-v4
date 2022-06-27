package com.thundashop.core.gotohub.dto;

import com.thundashop.core.common.DataCommon;
import lombok.Data;

/**
 * @author Naim Murad (naim)
 * @since 6/23/22
 */
@Data
public class GoToConfiguration extends DataCommon {
    public String authToken = "B?E(H+MbQeThVmYq";
    public String paymentTypeId;
    public int cuttOffHours;
}