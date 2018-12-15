
package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.pmsmanager.PmsBookingAddonItem;
import java.util.Date;

public class AddonItem {
    public Double price;
    public String name;
    public String descriptionWeb;
    public Integer count;
    public String productId;
    public Date date;
    public boolean isAdded = false;
    public Integer addedCount = 0;
    public Integer maxAddonCount = 0;
    public String icon = "";

    void setAddon(PmsBookingAddonItem item) {
        count = item.count;
        name = item.getName();
        price = item.price;
        productId = item.productId;
        date = item.date;
        maxAddonCount = item.count;
        descriptionWeb = item.descriptionWeb;
    }
}
