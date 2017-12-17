
package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import java.util.Date;

public class AddonItem {
    public Double price;
    public String name;
    public Integer count;
    public String productId;
    public Date date;
    public boolean isAdded = false;

    void setAddon(PmsBookingAddonItem item) {
        count = item.count;
        name = item.name;
        price = item.price;
        productId = item.productId;
        date = item.date;
    }
}
