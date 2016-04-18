
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PmsRoomSimple implements Serializable {
    public String bookingId = "";
    public String pmsRoomId = "";
    public String bookingItemId = "";
    public String owner = "";
    public List<PmsGuests> guest = new ArrayList();
    public long start;
    public long end;
    public String code = "";
    public String room ="";
    public String progressState = "";
    public boolean paidFor = false;
    public boolean transferredToArx = false;
    public boolean roomCleaned = false;
}
