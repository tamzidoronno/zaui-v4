
package com.thundashop.core.wubook;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class WubookBookedRoom implements Serializable {
    int guest = 0;
    int roomId = 0;
    String guestName = "";
    public HashMap<Date, Double> priceMatrix = new HashMap();
    int breakfasts;
    HashMap<String, Integer> addonsToAdd = new HashMap();
    public boolean needToAddTaxes = false;
}
