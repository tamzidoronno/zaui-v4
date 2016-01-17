
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsRepeatingData implements Serializable {
    public TimeRepeaterData data;
    public String repeattype = "";
    public String bookingItemId = "";
    public String bookingTypeId = "";
}
