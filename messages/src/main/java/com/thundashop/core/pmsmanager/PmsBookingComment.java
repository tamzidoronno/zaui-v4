
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class PmsBookingComment implements Serializable {
    public String userId = "";
    public String comment = "";
    public Date added = new Date();
    public boolean deleted = false;
    public boolean pinned = false;
    public String pmsBookingRoomId = "";
    public String commentId = UUID.randomUUID().toString();
    public HashMap<Long, String> modifiedByUser = new HashMap<>();
}
