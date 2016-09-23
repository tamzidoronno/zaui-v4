
package com.thundashop.core.wubook;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class WubookBookedRoom implements Serializable {
    int guest = 0;
    int roomId = 0;
    HashMap<Date, Double> priceMatrix = new HashMap();
}
