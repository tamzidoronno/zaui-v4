package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class PmsCareTaker extends DataCommon {
    public String description;
    public Date dateCompleted;
    public String inventoryProductId;
    //Bookingengine itemid.
    public String roomId;
    
    @Transient
    public String roomName;
    @Transient
    public String productName;
    @Transient
    public boolean completed = false;
}
