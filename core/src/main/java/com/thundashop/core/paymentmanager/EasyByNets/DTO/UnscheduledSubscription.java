
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UnscheduledSubscription {

    @SerializedName("unscheduledSubscriptionId")
    @Expose
    private String unscheduledSubscriptionId;

    public String getUnscheduledSubscriptionId() {
        return unscheduledSubscriptionId;
    }

    public void setUnscheduledSubscriptionId(String unscheduledSubscriptionId) {
        this.unscheduledSubscriptionId = unscheduledSubscriptionId;
    }

}
