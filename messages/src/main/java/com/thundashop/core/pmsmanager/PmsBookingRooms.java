
package com.thundashop.core.pmsmanager;

import com.thundashop.core.annotations.ExcludePersonalInformation;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.getshoplocksystem.LockCode;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import org.mongodb.morphia.annotations.Transient;

import java.io.Serializable;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PmsBookingRooms implements Serializable {

    public static Integer getNumberOfDays(Date start, Date end) {
        PmsBookingRooms room = new PmsBookingRooms();
        room.date.start = start;
        room.date.end = end;
        return room.getNumberOfNights();
    }

    public String shortId = "";
    public String bookingItemTypeId = "";
    public String bookingItemId = "";
    public String preferredBookingItemId = "";
    public String orderUnderConstructionId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    @ExcludePersonalInformation
    public List<PmsGuests> guests = new ArrayList<>();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 1;
    public double count = 1;
    public Double price = 0.0;
    Double priceWithoutDiscount = 0.0;
    public Date roomCreatedDate = new Date();
    public LinkedHashMap<String, Double> priceMatrix = new LinkedHashMap<>();
    public double taxes = 8;
    public String bookingId;
    public List<PmsBookingAddonItem> addons = new ArrayList<>();
    //added for zaui activity
    public List<BookingZauiActivityItem> zauiActivityItems = new ArrayList<>();
    public String currency = "NOK";
    public String cleaningComment = "";
    
    private String unsettledChecksum = "";

    public boolean nonrefundable = false;    
    public boolean checkedin = false;
    public boolean checkedout = false;
    public boolean blocked = false;
    public boolean triedToAutoAssign = false;
    public boolean warnedAboutAutoAssigning = false;
    
    @Editor
    public String code = "";
    
    @Editor
    public LockCode codeObject = null;
    
    @Editor
    public List<LockCode> codeObjectHistory = new ArrayList<>();
    
    public String cardformat = "";
    public Integer intervalCleaning = null;
    public boolean addedByRepeater = false;
    public Date invoicedTo = null;
    public Date invoicedFrom = null;
    public boolean keyIsReturned = false;
    
    //Processor stuff.
    public boolean ended = false;
    public boolean sentCloseSignal = false;
    public boolean prioritizeInWaitingList = false;
    public List<String> notificationsSent = new ArrayList<>();
    public boolean addedToArx = false;
    boolean canBeAdded = true;
    boolean isAddon = false;
    Date forcedOpenDate;
    boolean forcedOpenNeedClosing = false;
    public Date warnedAboutAutoExtend = null;
    public boolean credited;
    public boolean deleted = false;
    public Date deletedDate = new Date();
    public double totalCost = 0.0;
    public Date requestedEndDate = null;
    public Date undeletedDate;
    public boolean forceUpdateLocks = false;
    public boolean deletedByChannelManagerForModification = false;
    public boolean inWorkSpace = false;
    public boolean addedToWaitingList = false;
    private boolean overbooking = false;
    public Date lastBookingChangedItem;
    
    @Transient
    public Boolean isUsingNewBookingEngine = null;
    
    /**
     * Finalized entries
     */
    @Transient
    public Booking booking;
    @Transient
    public BookingItem item;
    @Transient
    public BookingItemType type;
    @Transient
    public Integer maxNumberOfGuests = 0;
    @Transient
    boolean paidFor = false;
    public boolean forceAccess = false;
    Double printablePrice = 0.0;
    
    /**
     * If this is set to true the system will automatically
     * create orders for the room when before the zreport is saved.
     */
    public boolean createOrdersOnZReport = false;
    
       
    @Administrator
    public String token = UUID.randomUUID().toString();
    
    public boolean loggedGetCode = false;
    public boolean loggedDeletedCode = false;
    public String language = "";
    public String countryCode = "";
    
    /**
     * The current unsettled amount ( Excluded accrued payments)
     * Note: This value is calculated when the bookings are being saved.
     */
    public Double unsettledAmount = null;
    
    
    /**
     * The current unsettled amount ( Excluded accrued payments)
     * Note: This value is calculated when the bookings are being saved.
     */
    public Double unsettledAmountIncAccrued = null;
    
    double unpaidAmount = 0.0;
    

    public boolean isOverBooking() {
        if(isAddedToBookingEngine()) {
            overbooking = false;
        }
        return overbooking;
    }
    
    void clear() {
        pmsBookingRoomId = UUID.randomUUID().toString();
        date.start = new Date();
        addedToArx = false;
        canBeAdded = true;
        bookingId = null;
        bookingItemId = "";
        code = "";
        invoicedFrom = null;
        invoicedTo = null;
        booking = null;
    }
    
    public boolean recentlyChangedBookingItem() {
        if(lastBookingChangedItem == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -5);
        return lastBookingChangedItem.after(cal.getTime());
    }
    
    public static String getOffsetKey(Calendar calStart, Integer priceType) {
        String offset = "";
        if(priceType == PmsBookingConstant.PriceType.daily ||
                priceType == PmsBookingConstant.PriceType.interval ||
                priceType == PmsBookingConstant.PriceType.progressive) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            offset = formatter.format(calStart.getTime());
        } else if(priceType == PmsBookingConstant.PriceType.monthly) {
            offset = calStart.get(Calendar.MONTH) + "-" + calStart.get(Calendar.YEAR);
        }
        return offset;
    }
    
    public Integer getNumberOfDays() {
        int days = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.start);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        while(true) {
            days++;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(date.end)) {
                break;
            }
        }
        return days;
    }

    Integer getNumberOfNights() {
        int days = 0;
        if(date.end == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.start);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        while(true) {
            days++;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(date.end)) {
                break;
            }
        }
        return days;        
    }    
    
    public Integer getNumberOfAdults() {
        int adults = 0;
        for(int i = 0; i < numberOfGuests; i++) {
            if(guests.size() > i) {
                PmsGuests guest = guests.get(i);
                if(!guest.isChild) {
                    adults++;
                }
            } else {
                adults++;
            }
        }
        return adults;
    }
    
    public Integer getNumberOfChildren() {
        int children = 0;
        for(int i = 0; i < numberOfGuests; i++) {
            if(guests.size() > i) {
                PmsGuests guest = guests.get(i);
                if(guest.isChild) {
                    children++;
                }
            }
        }
        return children;
    }
    
    public static Date convertOffsetToDate(String offset) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            return formatter.parse(offset);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    boolean isActiveOnDay(Date time) {
        if(date.end == null || date.start == null) {
            return false;
        }
        
        if(isStartingToday(time)) {
            return true;
        }
        if(isEndingToday(time)) {
            return false;
        }
        
        if(time.after(date.start) && time.before(date.end)) {
            return true;
        }
        
        return false;
    }
    
    public Double getDailyPrice(Integer type, Calendar cal) {
        int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        if(type.equals(PmsBookingConstant.PriceType.monthly)) {
            return price / days;
        }
        
        if(type.equals(PmsBookingConstant.PriceType.daily)) {
            return price;
        }
        
        if(type.equals(PmsBookingConstant.PriceType.interval)) {
            return price;
        }
        
        if(type.equals(PmsBookingConstant.PriceType.progressive)) {
            return price;
        }
        
        throw new UnsupportedOperationException("Not implented yet");
    }

    public boolean isEnded() {
        Date now = new Date();
        return isEnded(now);
    }

    boolean isEnded(Date day) {
        if(day == null) {
            return false;
        }
        boolean result = date.end != null && day.after(date.end);
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
            if(item2!= null && item2.addonType != null && item2.addonType == type) {
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
        if(filter.endInvoiceAt == null) {
            return new Date();
        }
        Date toEnd = new Date(filter.endInvoiceAt.getTime());
        
        if(booking.periodesToCreateOrderOn != null) {
            toEnd = addTimeUnits(booking.periodesToCreateOrderOn, booking, getInvoiceStartDate());
        }
        
        if(date.end.before(toEnd)) {
            return date.end;
        }
        
        return toEnd;
    }

    private Date addTimeUnits(Integer increaseUnits, PmsBooking booking, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(booking.priceType.equals(PmsBookingConstant.PriceType.monthly)) {
            cal.add(Calendar.MONTH, increaseUnits);
        }
        if(booking.priceType.equals(PmsBookingConstant.PriceType.daily)) {
            cal.add(Calendar.DAY_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBookingConstant.PriceType.weekly)) {
            cal.add(Calendar.WEEK_OF_YEAR, increaseUnits);
        }
        if(booking.priceType.equals(PmsBookingConstant.PriceType.hourly)) {
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
        
        if (isInBookingId(searchWord)) {
            return true;
        }
        
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
                    String phoneWithoutStartingZeros = searchWord.replaceFirst("^0+(?!$)", "");
                    if(guest.phone.toLowerCase().contains(phoneWithoutStartingZeros)) {
                        return true;
                    }
                    
                    if(guest.prefix != null && !guest.prefix.isEmpty()) {
                        String leftPaddedPrefix = "00"+guest.prefix;
                        String longNumberWithPrefix = leftPaddedPrefix + guest.phone.toLowerCase();
                        if(longNumberWithPrefix.toLowerCase().contains(searchWord)) {
                            return true;
                        }
                        
                        longNumberWithPrefix = leftPaddedPrefix + longNumberWithPrefix;
                        if(longNumberWithPrefix.toLowerCase().contains(searchWord)) {
                            return true;
                        }
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
    
    public static boolean isSameDayStatic(Date date1, Date date2) {
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
        if(date != null && date.start != null && (date.start.after(startDate) && date.start.before(endDate))) {
            return true;
        }
        return false;
    }

    boolean checkingOutBetween(Date startDate, Date endDate) {
        if((date.end.after(startDate) && date.end.before(endDate))) {
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

    boolean startingBetween(Date start, Date end) {
        boolean isAfterStart = date.start.after(start);
        boolean isBeforeEnd = date.start.before(end);
        
        boolean res = isAfterStart && isBeforeEnd;
        return res;
    }

    void updateItem(PmsBookingAddonItem item) {
        for(PmsBookingAddonItem addedItem : addons) {
            if(item.addonId.equals(addedItem.addonId)) {
                addedItem.price = item.price;
                addedItem.count = item.count;
                addedItem.isIncludedInRoomPrice = item.isIncludedInRoomPrice;
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

    PmsBookingAddonItem hasAddon(String productId, Date date) {
        for(PmsBookingAddonItem addon : addons) {
            if(addon.productId.equals(productId) && isSameDay(date, addon.date)) {
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
        List<PmsBookingAddonItem> result = new ArrayList<>();
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
        undeletedDate = new Date();
        addedToWaitingList = false;
        inWorkSpace = false;
        overbooking = false;
        deletedByChannelManagerForModification = false;
    }

    public void delete() {
        deleted = true;
        deletedDate = new Date();
        overbooking = false;
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
        
        if(priceMatrix.keySet().size() > 0) {
            price = sum / priceMatrix.keySet().size();
        }
    }

    void updateBreakfastCount() {
        for(PmsBookingAddonItem item : addons) {
            
            if(item.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST || item.dependsOnGuestCount) {
                item.count = numberOfGuests;
            }
        }
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
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date.start);
        String startString = startCal.get(Calendar.DAY_OF_YEAR) + "-" + startCal.get(Calendar.YEAR);
        String nowString = cal.get(Calendar.DAY_OF_YEAR) + "-" + cal.get(Calendar.YEAR);
        
        if(startString.equals(nowString)) {
            return true;
        }
        
        return false;
    }

    public double calculateNonRefundAddons() {
        double total = 0.0;

        for(PmsBookingAddonItem item : addons) {
            if(!item.noRefundable) {
                continue;
            }
            total += (item.price * item.count);
        }
        
        return total;
    }
    
    public void calculateTotalCost(Integer priceType) {
        totalCost = 0.0;
        Integer days = getNumberOfNights();

        if(priceType.equals(PmsBookingConstant.PriceType.monthly)) {
            days = getNumberOfMonths();
        }

        if(priceType.equals(PmsBookingConstant.PriceType.daily)) {
            if(priceMatrix != null && !priceMatrix.isEmpty()) {
                for(Double price : priceMatrix.values()) {
                    if(isDeleted() && (!nonrefundable || deletedByChannelManagerForModification)) {
                        continue;
                    }
                    totalCost += price;
                }
            } else {
                totalCost = 0;
            }
        } else {
            totalCost += days * price;
        }
        for(PmsBookingAddonItem item : addons) {
            if(item.isIncludedInRoomPrice) {
                continue;
            }
            if(isDeleted() && ((!item.noRefundable && !nonrefundable) || deletedByChannelManagerForModification)) {
                continue;
            }
            if (item.price != null && item.count != null) {
                totalCost += (item.price * item.count);
            }
        }
        
        if(isDeleted() && (!nonrefundable || deletedByChannelManagerForModification)) {
            price = 0.0;
        } else {
            if(priceType.equals(PmsBookingConstant.PriceType.daily)) {
                double cost = 0.0;
                for(Double price : priceMatrix.values()) {
                    cost += price;
                }
                if(priceMatrix.keySet().size() > 0) {
                    price = cost / priceMatrix.keySet().size();
                }
            }
        }
    }

    public boolean hasAddonOfType(String type) {
        return addons.stream().anyMatch(addon -> addon.addedBy != null 
                && addon.addedBy.equals(type) 
                && addon.count > 0 
                && addon.price > 0
        );
    }

    public boolean decreaseAddonAndRemoveIfEmpty(String addonId) {
        for (PmsBookingAddonItem addon : addons) {
            if (addon.addonId.equals(addonId)) {
                if (addon.count > 1) {
                    addon.count -= 1;
                    return true;
                } else {
                    addons.remove(addon);
                    return true;
                }
            }
        }
        
        return false;
    }

    private int getNumberOfMonths() {
        Date startDate = date.start;
        if(date.end == null) {
            return 0;
        }
        Date endDate = date.end;
        int months = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        while(true) {
            cal.add(Calendar.MONTH, 1);
            if(cal.getTime().after(endDate)) {
                break;
            }
            if(cal.getTime().equals(endDate)) {
                break;
            }
            months++;
        }
        return months;
    }

    boolean requestedEndDate(Date startDate, Date endDate) {
        if(requestedEndDate != null && requestedEndDate.after(startDate) && requestedEndDate.before(endDate)) {
            return true;
        }
        return false;
    }
    
    private boolean isInBookingId(String searchWord) {
        List<String> ids = Arrays.asList(searchWord.split(","));

        if (ids.contains(bookingId)) {
            return true;
        }
        return false;
    }

    static String convertOffsetToString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getOffsetKey(cal, PmsBookingConstant.PriceType.daily);
    }
    
    void checkAddons() {
        Calendar cal = Calendar.getInstance();
        for(PmsBookingAddonItem item : addons) {
            if (item.count == null) {
                item.count = 0;
            }
            
            if (item.price == null || item.price.isNaN() || item.price.isInfinite()) {
                item.price = 0D;
            }
            if(item.percentagePrice > 0) {
                cal.setTime(item.date);
                String dateOffset = PmsBookingRooms.convertOffsetToString(item.date);
                Double dailyPrice = priceMatrix.get(dateOffset);
                if(dailyPrice != null) {
                    double dayprice = dailyPrice * ((double)item.percentagePrice / 100);
                    item.price = dayprice;
                }
            }
        }
    }

    public void removeCode() {
        code = "";
        codeObject = null;
    }

    public Integer setGuestAsChildren(Integer children) {
        int childrenSet = 0;
        if(guests.size() < numberOfGuests) {
            for(int i = guests.size(); i < numberOfGuests; i++) {
                guests.add(new PmsGuests());
            }
        }
        
        for(int i = numberOfGuests-children; i < numberOfGuests;i++) {
            if(guests.size() > i && guests.get(i) != null) {
                guests.get(i).isChild = true;
                childrenSet++;
            }
        }
        return childrenSet;
    }

    boolean bookingConnected() {
        if(bookingId == null || bookingId.isEmpty()) {
            return false;
        }
        return true;
    }


    boolean checkingOutAtDay(Date time) {
        if(date == null || date.end == null) {
            return false;
        }
        return isSameDay(date.end, time);
    }

    boolean checkingInAtDay(Date time) {
        if(date == null || date.start == null) {
            return false;
        }
        return isSameDay(date.start, time);
    }

    Double getPriceForDayWithoutAddonsIncluded(Integer priceType, Calendar cal) {
        double removeFromPrice = 0.0;
        for(PmsBookingAddonItem item : addons) {
            if(!item.isIncludedInRoomPrice) {
                continue;
            }
            if(sameDay(cal.getTime(), item.date)) {
                removeFromPrice += (item.price * item.count);
            }
        }
        double toreturn = getPriceForDay(priceType, cal);
        toreturn -= removeFromPrice;
        return toreturn;
    }

    public boolean isAddedToBookingEngine() {
        if(bookingId != null && ! bookingId.isEmpty() && !isDeleted()) {
            return true;
        }
        return false;
    }

    public void unmarkOverBooking() {
        overbooking = false;
    }

    void markAsOverbooking() {
        overbooking = true;
    }

    boolean hasAddonOfProduct(String productId) {
        for(PmsBookingAddonItem item : addons) {
            if(item.productId != null && item.productId.equals(productId)) {
                return true;
            }
        }
        return false;
    }

    boolean beforeFirstOfJan2019() {
        
        if(pmsBookingRoomId != null && pmsBookingRoomId.equals("0f1976ae-1668-4106-9c56-48ec422bdbac")) {
            return false;
        }
        if(pmsBookingRoomId != null && pmsBookingRoomId.equals("ac80abc8-2457-4269-8993-554a9aef886e")) {
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        return date.start.before(cal.getTime());
    }

    boolean isRecentlyCreated() {
        long diff = new Date().getTime() - roomCreatedDate.getTime();
        return diff < 60000;
    }

    boolean isCreatedLastMinutes(Integer minutes) {
        long diff = new Date().getTime() - roomCreatedDate.getTime();
        return diff < (60000 * minutes);
    }

    public boolean hasUnsettledAmount() {
        if (unsettledAmount == null)
            return false;
        
        return unsettledAmount > 0.5 || unsettledAmount < -0.5;
    }
    
    public boolean hasUnsettledAmountIncAccrued() {
        if (unsettledAmountIncAccrued == null || unsettledAmountIncAccrued == 0.0)
            return false;
        return unsettledAmountIncAccrued > 0.5 || unsettledAmountIncAccrued < -0.5;
    }

    boolean isPmsConferenceRoom() {
        return (bookingItemTypeId != null && bookingItemTypeId.equals("gspmsconference"));
    }

    boolean needToCalculateUnsettledAmount(List<Order> orders, PmsBooking pmsBooking) throws Exception {
        String checksum = calculateCheckSum();

        for(Order order : orders) {
            if (order == null) {
                System.out.println("ERROR. Could not find connected order for booking id: " + pmsBooking.incrementBookingId + ".");
                continue;
            }
            for(CartItem item : order.getCartItems()) {
                if(item.getProduct().externalReferenceId.equals(pmsBookingRoomId)) {
                    checksum += item.getCartItemId() + "_" + item.getProductPrice()+ "_" + item.getCount();
                }
            }
            checksum += "_" + order.status;
        }
        
        checksum += "_" + numberOfGuests;
        
        if(guests != null) {
            checksum += "_" + guests.size();
        }
        
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(checksum.getBytes());
        checksum = new String(thedigest);
        
        boolean needUpdate = !checksum.equals(unsettledChecksum);
        unsettledChecksum = checksum;
        return needUpdate;
    }

    private String calculateCheckSum() {
        String sum = "";
        for(String day : priceMatrix.keySet()) {
            sum += day + "_" + priceMatrix.get(day);
        }
        
        for(PmsBookingAddonItem item : addons) {
            sum += item.getKey() + "_" + item.count + "_" + item.price + "_" + item.priceExTaxes + "_" + isDeleted() + "_" + nonrefundable;
        }
        
        return sum;
    }
}
