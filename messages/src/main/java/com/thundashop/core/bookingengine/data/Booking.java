package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class Booking extends DataCommon implements Comparable<Booking> {
    public String bookingItemId = "";
    public int incrementalBookingId = 0;
    public boolean bookingDeleted = false;
    
    public String bookingItemTypeId = "";
    
    public Date startDate;
    public Date endDate;

    public boolean needConfirmation = false;
    public String externalReference;
    public String userId;
    
    /**
     * Where did this booking come from ?
     */
    public String source = "";
    
    public String getInformation() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");
        try {
            return "[Itemid=" + bookingItemId+",incrementalBookingId="+incrementalBookingId+",bookingItemTypeId="+bookingItemTypeId+",startDate="+dateFormat.format(startDate)+",endDate="+dateFormat.format(endDate)+"]";
        } catch (NullPointerException ex) {
            return "";
        }
    }
    
    public String getHumanReadableDates() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");
        if (endDate != null && startDate != null) {
            return dateFormat.format(startDate)+" - "+dateFormat.format(endDate);
        } 
        
        return "";
    }

    // Got this solution from: http://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
    public boolean interCepts(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || this.startDate == null || this.endDate == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.startDate.getTime()+1;
        long EndDate1 = endDate.getTime();
        long EndDate2 = this.endDate.getTime()-1;
        return (StartDate1 <= EndDate2) && (StartDate2 <= EndDate1);
    }
    
    public boolean within(Date startDate, Date endDate) { 
        if (startDate == null || endDate == null || this.startDate == null || this.endDate == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.startDate.getTime();
        long EndDate1 = endDate.getTime(); 
        long EndDate2 = this.endDate.getTime();
        return (StartDate1 < EndDate2) && (StartDate2 < EndDate1);
    }

    public boolean completlyWithin(Date start, Date end) {
        if (start == null || end == null || this.startDate == null || this.endDate == null) {
            return false;
        }
        
        if (startDate.equals(start) && endDate.equals(end))
            return true;
        
        if (start.after(startDate) && end.before(endDate))
            return true;
        
        if (start.equals(startDate) && end.before(endDate))
            return true;

        if (start.after(startDate) && end.equals(endDate))
            return true;

        return false;
    }

    @Override
    public int compareTo(Booking o) {
        return startDate.compareTo(o.startDate);
    }
}
