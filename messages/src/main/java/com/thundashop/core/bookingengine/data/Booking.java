package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class Booking extends DataCommon implements Comparable<Booking> {
    public String bookingItemId = "";
    public int incrementalBookingId = 0;
    public boolean bookingDeleted = false;
    
    public String bookingItemTypeId = "";
    public String prevAssignedBookingItemId = null;
    
    public Date startDate;
    public Date endDate;

    public boolean needConfirmation = false;
    public String externalReference;
    public String userId;
    
    /**
     * Where did this booking come from ?
     */
    public String source = "";
    public String doneByUserId = "";
    public String doneByImpersonator = "";
    
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

    public boolean isUnassigned() {
        return bookingItemId == null || bookingItemId.isEmpty();
    }
    
    public static Comparator sortByStartDate() {
    
        return new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                if (o1.startDate.equals(o2.startDate)) {
                    return o2.endDate.compareTo(o1.endDate);
                }

                return o1.startDate.compareTo(o2.startDate);
            }
        };
    }

    public Long getStayLength() {
        if (startDate == null || endDate == null)
            return 0L;
        
        return endDate.getTime() - startDate.getTime();
    }
    
    public static Comparator sortStayLength() {
    
        return new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                return o1.getStayLength().compareTo(o2.getStayLength());
            }
        };
    }
    
    public static Comparator sortStayLength(List<Booking> bookings) {
        Map<String, Booking> bookingMap = new HashMap<String, Booking>();
        
        bookings.stream()
                .forEach(o -> bookingMap.put(o.id, o));
        
        return new Comparator<String>() {
            @Override
            public int compare(String id1, String id2) {
                Booking o1 = bookingMap.get(id1);
                Booking o2 = bookingMap.get(id2);
                return o1.getStayLength().compareTo(o2.getStayLength());
            }
        };
    }
    
    public boolean startsTomorrowOrLater() {
        if (startDate  == null)
            return false;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        
        Date toCompareDate = cal.getTime();
        
        return startDate.after(toCompareDate);
    }

    public String getSourceState() {
        if(source != null && source.equals("cleaning")) {
            return "cleaning";
        }
        
        if(source != null && source.toLowerCase().contains("closed")) {
            return "closed";
        }
        
        return "normal";
    }

    public boolean isAssigned() {
        return !isUnassigned();
    }

    public void stripSeconds() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();
        
        cal.setTime(endDate);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        endDate = cal.getTime();
        
        
    }
}
