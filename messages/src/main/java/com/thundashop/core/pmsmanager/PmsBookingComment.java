
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsBookingComment implements Serializable {
    public String userId = "";
    public String comment = "";
    public Date added = new Date();
    public boolean deleted = false;
    public String pmsBookingRoomId = "";
}
