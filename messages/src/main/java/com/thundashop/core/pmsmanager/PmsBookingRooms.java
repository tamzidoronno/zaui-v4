
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.Editor;
import com.thundashop.core.pmsmanager.PmsBooking.PriceType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date; 
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class PmsBookingRooms implements Serializable {
    public String bookingItemTypeId = "";
    public String bookingItemId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    public List<PmsGuests> guests = new ArrayList();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 1;
    public double count = 1;
    public Double price = 0.0;
    public LinkedHashMap<String, Double> priceMatrix = new LinkedHashMap();
    public double taxes = 8;
    public String bookingId;
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public String currency = "NOK";
    public String cleaningComment = "";
    
    public boolean checkedin = false;
    public boolean checkedout = false;
    
    @Editor
    public String code = "";
    public String cardformat = "";
    public Integer intervalCleaning = null;
    public boolean addedByRepeater = false;
    public Date invoicedTo = null;
    public Date invoicedFrom = null;
    public boolean keyIsReturned = false;
    
    //Processor stuff.
    public boolean ended = false;
    public boolean sentCloseSignal = false;
    public List<String> notificationsSent = new ArrayList();
    public boolean addedToArx = false;
    boolean canBeAdded = true;
    boolean isAddon = false;
    Date forcedOpenDate;
    boolean forcedOpenNeedClosing = false;
    public Date warnedAboutAutoExtend = null;
    public boolean credited;
    public boolean deleted = false;
    public Date deletedDate = new Date();
    
    /**
     * Finalized entries
     */
    @Transient
    public Booking booking;
    @Transient
    public BookingItem item;
    @Transient
    public BookingItemType type;
    
    void clear() {
        pmsBookingRoomId = UUID.randomUUID().toString();
        date.start = new Date();
        addedToArx = false;
        canBeAdded = true;
        bookingId = "";
        bookingItemId = "";
        code = "";
    }
    
    public static String getOffsetKey(Calendar calStart, Integer priceType) {
        String offset = "";
        if(priceType == PmsBooking.PriceType.daily || 
                priceType == PmsBooking.PriceType.interval || 
                priceType == PmsBooking.PriceType.progressive) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            offset = formatter.format(calStart.getTime());
        } else if(priceType == PmsBooking.PriceType.monthly) {
            offset = calStart.get(Calendar.MONTH) + "-" + calStart.get(Calendar.YEAR);
        }
        return offset;
    }
    
    public Integer getNumberOfDays() {
        int days = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.start);
        while(true) {
            days++;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(date.end)) {
                break;
            }
        }
        return days;
    }
    
    boolean isActiveOnDay(Date time) {
        if(date.end == null || date.start == null) {
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        if(cal.getTime().after(date.start) && cal.getTime().before(date.end)) {
            return true;
        }
        
        return false;
    }
    
    public Double getDailyPrice(Integer type, Calendar cal) {
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        if(type.equals(PriceType.monthly)) {
            return price / days;
        }
        
        if(type.equals(PriceType.daily)) {
            return price;
        }
        
        if(type.equals(PriceType.interval)) {
            return price;
        }
        
        if(type.equals(PriceType.progressive)) {
            return price;
        }
        
        throw new UnsupportedOperationException("Not implented yet");
    }

    public boolean isEnded() {
        Date now = new Date();
        return isEnded(now);
    }

    boolean isEnded(Date day) {
        boolean result = day.after(date.end);
        return result;
    }

    public boolean isStarted() {
        return isStarted(new Date());
    }
    
    boolean isStarted(Date when) {
        return (date.start.before(when) || date.start.equals(when));
    }

    public void clearAddonType(int type) {
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        for(PmsBookingAddonItem item2 : addons) {
            if(item2.addonType == type) {
                toRemove.add(item2);
            }
        }
        
        addons.removeAll(toRemove);
    }
    
    boolean isStartingToday() {
        if(date.start == null) {
            return false;
        }
        return isStartingToday(new Date());
    }

    boolean isEndingToday() {
        if(date.end == null) {
            return false;
        }
        return isEndingToday(new Date());
    }

    boolean isEndingToday(Date toDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.end);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }

    boolean isSame(PmsBookingRooms alreadyAdded) {
        if(!alreadyAdded.bookingItemTypeId.equals(bookingItemTypeId)) {
            return false;
        }
        if(alreadyAdded.date.start.equals(date.start) && alreadyAdded.date.end.equals(date.end)) {
            return true;
        }
        return false;
    }

    boolean needInvoicing(NewOrderFilter filter) {
        if(filter.forceInvoicing) {
            return true;
        }
        
        if(filter.onlyEnded && date.end.after(filter.endInvoiceAt)) {
            return false;
        }
        
        if(filter.onlyEnded && sameDay(new Date(), date.end)) {
            return false;
        }
        
        Date toInvoiceFrom = invoicedTo;
        if(filter.prepayment && invoicedTo != null) {
            //If invoiced into the future. 
            if(filter.prepaymentDaysAhead >= 0) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(invoicedTo);
                cal.add(Calendar.DAY_OF_YEAR, filter.prepaymentDaysAhead*-1);
                toInvoiceFrom = cal.getTime();
            }
        }
        
        //Never autocreate orders that is in the future.
        Date max = new Date();
        if(filter.maxAutoCreateDate != null && filter.autoGeneration) {
            max = filter.maxAutoCreateDate;
        }
        
        if(toInvoiceFrom != null && toInvoiceFrom.after(max)) {
            return false;
        }
        
        
        if(invoicedTo == null) {
            return true;
        }
        
        if(date.end.before(invoicedTo) || sameDay(date.end, invoicedTo)) {
            return false;
        }
        
        if(toInvoiceFrom.before(filter.endInvoiceAt) && !sameDay(toInvoiceFrom, filter.endInvoiceAt)) {
            return true;
        }
        
        return false;
    }
    

    Date getInvoiceStartDate() {
        
        if(invoicedTo == null) {
            return date.start;
        }
        
        if(date.start.after(invoicedTo)) {
            return date.start;
        }
        
        return invoicedTo;
    }

    Date getInvoiceEndDate(NewOrderFilter filter, PmsBooking booking) {
        if(filter.increaseUnits > 0) {
            filter.endInvoiceAt = addTimeUnits(filter.increaseUnits, booking, getInvoiceStartDate());
        }
        
        if(date.end.before(filter.endInvoiceAt)) {
            return date.end;
        }
        
        return filter.endInvoiceAt;
    }

    private Date addTimeUnits(Integer increaseUnits, PmsBooking booking, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            cal.add(Calendar.MONTH, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.daily)) {
            cal.add(Calendar.DAY_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.weekly)) {
            cal.add(Calendar.WEEK_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBooking.PriceType.hourly)) {
            cal.add(Calendar.HOUR_OF_DAY, increaseUnits);
        }
        return cal.getTime();
    }

    private boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    boolean containsSearchWord(String searchWord) {
        searchWord = searchWord.toLowerCase();
        for(PmsGuests guest : guests) {
            if(searchWord != null && searchWord.contains("@")) {
                if(guest.email != null && guest.email.toLowerCase().contains(searchWord)) {
                    return true;
                }
            }
            if(searchWord != null && searchWord.length() > 6) {
                if(guest.phone != null) {
                    if(guest.phone.toLowerCase().contains(searchWord)) {
                        return true;
                    }
                    if(guest.prefix != null) {
                        String testSearchWord = searchWord;
                        if(testSearchWord.startsWith("+")) {
                            testSearchWord = testSearchWord.substring(1);
                        }
                        
                        String toCheck = guest.prefix + guest.phone;
                        if(toCheck.startsWith("+")) {
                            toCheck = toCheck.substring(1);
                        }
                        if(toCheck.toLowerCase().contains(testSearchWord)) {
                            return true;
                        }
                    }
                }
            }
            if(guest.name != null && guest.name.toLowerCase().contains(searchWord)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSameDay(Date date1, Date date2) {
        if(date1 == null || date2 == null) {
            return false;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    boolean isActiveInPeriode(Date startDate, Date endDate) {
        if(date != null && date.end != null && date.start != null && date.start.before(endDate) && date.end.after(startDate)) {
            return true;
        }
        if(isSameDay(date.start, startDate)) {
            return true;
        }
        return false;
    }

    boolean checkingInBetween(Date startDate, Date endDate) {
        if((date.start.after(startDate) && date.start.before(endDate)) || 
            isSameDay(date.start, endDate) ||
            isSameDay(date.start, startDate)) {
            return true;
        }
        return false;
    }

    boolean checkingOutBetween(Date startDate, Date endDate) {
        if((date.end.after(startDate) && date.end.before(endDate)) || 
                isSameDay(date.end, endDate) ||
                isSameDay(date.end, startDate)) {
            return true;
        }
        return false;
    }

    boolean isEndedDaysAgo(int daysAgo) {
        if(date == null || date.end == null) {
            return false;
        }
        daysAgo *= -1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysAgo);
        return date.end.before(cal.getTime());
    }

    boolean startingInHours(int maxAhead) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, maxAhead * -1);
        return date.start.after(cal.getTime());
    }

    void updateItem(PmsBookingAddonItem item) {
        for(PmsBookingAddonItem addedItem : addons) {
            if(item.addonId.equals(addedItem.addonId)) {
                addedItem.price = item.price;
                addedItem.count = item.count;
            }
        }
    }

    PmsBookingAddonItem hasAddon(Integer key, Date date) {
        for(PmsBookingAddonItem addon : addons) {
            if(addon.addonType.equals(key) && isSameDay(date, addon.date)) {
                return addon;
            }
        }
        return null;
    }

    void sortAddonList() {
        Collections.sort(addons, new Comparator<PmsBookingAddonItem>(){
            public int compare(PmsBookingAddonItem o1, PmsBookingAddonItem o2){
                return o1.date.compareTo(o2.date);
            }
       });
    }

    List<PmsBookingAddonItem> getAllAddons(String productId, Date startDate, Date endDate) {
        List<PmsBookingAddonItem> result = new ArrayList();
        for(PmsBookingAddonItem addon : addons) {
            if(addon.productId.equals(productId)) {
                if(addon.addonType != null && addon.addonType.equals(PmsBookingAddonItem.AddonTypes.EARLYCHECKIN)) {
                    if(isSameDay(addon.date, startDate)) {
                        result.add(addon);        
                    }
                } else if(addon.addonType != null && addon.addonType.equals(PmsBookingAddonItem.AddonTypes.LATECHECKOUT)) {
                    if(isSameDay(addon.date, endDate)) {
                        result.add(addon);                    
                    }
                } else if(addon.date.after(startDate) && addon.date.before(endDate)) {
                    result.add(addon);
                } else if(isSameDay(addon.date, startDate)) {
                    result.add(addon);
                }
            }
        }
        
        return result;
    }

    public Double getPriceForDay(Integer priceType, Calendar time) {
        String offset = getOffsetKey(time, priceType);
        if(priceMatrix.containsKey(offset)) {
            return priceMatrix.get(offset);
        }
        return price;
    }

    void undelete() {
        deleted = false;
        deletedDate = null;
    }

    public void delete() {
        deleted = true;
        deletedDate = new Date();
    }

    public boolean isDeleted() {
        return deleted;
    }

    void copyDeleted(PmsBookingRooms room) {
        deleted = room.isDeleted();
        deletedDate = room.deletedDate;
    }

    void setBooking(Booking booking) {
        bookingId = booking.id;
        this.booking = booking;
    }

    void calculateAvgPrice() {
        double sum = 0.0;
        for(Double val : priceMatrix.values()) {
            sum += val;
        }
        
        price = sum / priceMatrix.keySet().size();
    }

    void updateBreakfastCount() {
        for(PmsBookingAddonItem item : addons) {
            if(item.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST) {
                item.count = numberOfGuests;
            }
        }
    }

    void removeAddonByType(Integer addonType) {
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        for(PmsBookingAddonItem item : addons) {
            if(item.addonType.equals(addonType)) {
                toRemove.add(item);
            }
        }
        
        addons.removeAll(toRemove);
    }

    void updateAddonCount(Integer type, Integer count) {
        for(PmsBookingAddonItem item : addons) {
            if(item.addonType.equals(type)) {
                item.count = count;
            }
        }
    }

    boolean isStartingToday(Date now) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        if(cal.get(Calendar.HOUR_OF_DAY) < 6) {
            return false;
        }
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.start);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }
}
