package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
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
            return "[Itemid=" + bookingItemId+",incrementalBookingId="+incrementalBookingId+",bookingItemTypeId="+bookingItemTypeId+",getStartDateTranslated()="+dateFormat.format(getStartDateTranslated())+",getEndDateTranslated()="+dateFormat.format(getEndDateTranslated())+"]";
        } catch (NullPointerException ex) {
            return "";
        }
    }
    
    public String getHumanReadableDates() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM-yyyy HH:mm:ss");
        if (getEndDateTranslated() != null && getStartDateTranslated() != null) {
            return dateFormat.format(getStartDateTranslated())+" - "+dateFormat.format(getEndDateTranslated());
        } 
        
        return "";
    }

    // Got this solution from: http://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
    public boolean interCepts(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || this.getStartDateTranslated() == null || this.getEndDateTranslated() == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.getStartDateTranslated().getTime()+1;
        long EndDate1 = endDate.getTime();
        long EndDate2 = this.getEndDateTranslated().getTime()-1;
        return (StartDate1 <= EndDate2) && (StartDate2 <= EndDate1);
    }
    
    public boolean within(Date startDate, Date endDate) { 
        if (startDate == null || endDate == null || this.getStartDateTranslated() == null || this.getEndDateTranslated() == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.getStartDateTranslated().getTime();
        long EndDate1 = endDate.getTime(); 
        long EndDate2 = this.getEndDateTranslated().getTime();
        return (StartDate1 < EndDate2) && (StartDate2 < EndDate1);
    }

    private boolean completlyWithin(Date start, Date end) {
        if (start == null || end == null || this.getStartDateTranslated() == null || this.getEndDateTranslated() == null) {
            return false;
        }
        
        if (getStartDateTranslated().equals(start) && getEndDateTranslated().equals(end))
            return true;
        
        if (start.after(getStartDateTranslated()) && end.before(getEndDateTranslated()))
            return true;
        
        if (start.equals(getStartDateTranslated()) && end.before(getEndDateTranslated()))
            return true;

        if (start.after(getStartDateTranslated()) && end.equals(getEndDateTranslated()))
            return true;

        return false;
    }

    @Override
    public int compareTo(Booking o) {
        return getStartDateTranslated().compareTo(o.getStartDateTranslated());
    }

    public boolean isUnassigned() {
        return bookingItemId == null || bookingItemId.isEmpty();
    }
    
    public static Comparator sortByStartDate() {
    
        return new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                if (o1.getStartDateTranslated().equals(o2.getStartDateTranslated())) {
                    return o2.getEndDateTranslated().compareTo(o1.getEndDateTranslated());
                }

                return o1.getStartDateTranslated().compareTo(o2.getStartDateTranslated());
            }
        };
    }

    public Long getStayLength() {
        if (getStartDateTranslated() == null || getEndDateTranslated() == null)
            return 0L;
        
        return getEndDateTranslated().getTime() - getStartDateTranslated().getTime();
    }
    
    private static Comparator sortStayLength() {
    
        return new Comparator<Booking>() {
            @Override
            public int compare(Booking o1, Booking o2) {
                return o1.getStayLength().compareTo(o2.getStayLength());
            }
        };
    }
    
    public boolean startsTomorrowOrLater() {
        if (getStartDateTranslated()  == null)
            return false;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        
        Date toCompareDate = cal.getTime();
        
        return getStartDateTranslated().after(toCompareDate);
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
    
    public Date getStartDateTranslated() {
        return startDate;
    }
    
    public Date getEndDateTranslated() {
        return endDate;
    }

    public void setStartDate(Date start) {
        this.startDate = start;
    }

    public void setEndDate(Date end) {
        this.endDate = end;
    }
}
