
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsRoomSimple implements Serializable {
    public String bookingId = "";
    public String pmsRoomId = "";
    public String bookingItemId = "";
    public String owner = "";
    public List<PmsGuests> guest = new ArrayList();
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public long start;
    public long end;
    public Integer numberOfGuests;
    public String code = "";
    public String room ="";
    public String progressState = "";
    public double price = 0.0;
    public boolean paidFor = false;
    public boolean transferredToArx = false;
    public boolean roomCleaned = false;
    public boolean checkedIn;
    public boolean checkedOut;
    public Date regDate;
}
