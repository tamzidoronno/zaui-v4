
package com.thundashop.core.pmsmanager;

import com.thundashop.core.annotations.ExcludePersonalInformation;
import com.thundashop.core.common.Editor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsRoomSimple implements Serializable {
    public String bookingId = "";
    public String bookingEngineId = "";
    public String pmsRoomId = "";
    public String bookingItemId = "";
    @ExcludePersonalInformation
    public String owner = "";
    public String ownerDesc = "";
    @ExcludePersonalInformation
    public String ownersEmail = "";
    @ExcludePersonalInformation
    public List<PmsGuests> guest = new ArrayList();
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public long start;
    public long end;
    public Integer numberOfGuests;
    
    @Editor
    public String code = "";
    public String room ="";
    public String roomType = "";
    public String progressState = "";
    public String channel = "";
    public String wubookreservationid = "";
    public double price = 0.0;
    public boolean paidFor = false;
    public boolean transferredToArx = false;
    public boolean roomCleaned = false;
    public boolean checkedIn;
    public boolean checkedOut;
    public boolean keyIsReturned = false;
    public boolean testReservation = false;
    public Integer numberOfRoomsInBooking = 0;
    public Date regDate;
    public Date invoicedTo;
    public String bookingTypeId;
    public String wubookchannelid;
    List<String> orderIds;
    public Integer numberOfNights;
    boolean createOrderAfterStay;
    boolean hasBeenCleaned = false;
    public double totalCost;
    public double totalUnpaidCost = 0.0;
    public String userId;
    public boolean nonrefundable = false;
    public boolean hasUnchargedPrePaidOrders = false;
    public HashMap<Long, PmsBookingComment> bookingComments = new HashMap(); 
    double totalUnsettledAmount;
    
    boolean earlycheckin = false;
    boolean latecheckout = false;
    boolean extrabed = false;
    boolean childbed = false;
    String cleaningComment = "";
    public Date requestedEndDate;
    public Integer priceType;
}
