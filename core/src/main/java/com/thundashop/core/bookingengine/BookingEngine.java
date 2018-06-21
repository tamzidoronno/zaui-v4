
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingEngine extends GetShopSessionBeanNamed implements IBookingEngine {

    @Autowired
    public BookingEngineAbstract bookingEngineAbstract;
    
    
    @Override
    public BookingItemType createABookingItemType(String name) {
        return deepClone(bookingEngineAbstract.createABookingItemType(name));
    }

    @Override
    public List<BookingItemType> getBookingItemTypes() {
        return deepClone(bookingEngineAbstract.getBookingItemTypes());
    }

    @Override
    public List<BookingItemType> getBookingItemTypesWithSystemType(Integer systemType) {
        return deepClone(bookingEngineAbstract.getBookingItemTypesWithSystemType(systemType));
    }

    @Override
    public BookingItemType updateBookingItemType(BookingItemType type) {
        return deepClone(bookingEngineAbstract.updateBookingItemType(type));
    }

    @Override
    public BookingItem saveBookingItem(BookingItem item) {
        return deepClone(bookingEngineAbstract.saveBookingItem(item));
    }

    @Override
    public BookingItem getBookingItem(String id) {
        return deepClone(bookingEngineAbstract.getBookingItem(id));
    }

    @Override
    public List<BookingItem> getBookingItems() {
        return deepClone(bookingEngineAbstract.getBookingItems());
    }

    @Override
    public void deleteBookingItem(String id) {
        bookingEngineAbstract.deleteBookingItem(id);
    }

    @Override
    public void setConfirmationRequired(boolean confirmationRequired) {
        bookingEngineAbstract.setConfirmationRequired(confirmationRequired);
    }

    @Override
    public BookingEngineConfiguration getConfig() {
        return deepClone(bookingEngineAbstract.getConfig());
    }

    @Override
    public List<Booking> getAllBookings() {
        return deepClone(bookingEngineAbstract.getAllBookings());
    }
    
    @Override
    public List<Booking> getAllBookingsByBookingItem(String bookingItemId) {
        return deepClone(bookingEngineAbstract.getAllBookingsByBookingItem(bookingItemId));
    }
    
    @Override
    public List<Booking> getAllBookingsByBookingItemInDateRange(String bookingItemId, Date start, Date end) {
        List<Booking> bookings = getAllBookingsByBookingItem(bookingItemId);
        bookings.stream().filter(item -> item.interCepts(start,end)).collect(Collectors.toList());
        return bookings;
    }
    
    public void removeBookingsWhereUserHasBeenDeleted(String bookingItemId) {
        bookingEngineAbstract.removeBookingsWhereUserHasBeenDeleted(bookingItemId);
    }

    @Override
    public void changeTypeOnBooking(String bookingId, String itemTypeId) {
        bookingEngineAbstract.changeTypeOnBooking(bookingId, itemTypeId);
    }

    @Override
    public void changeDatesOnBooking(String bookingId, Date start, Date end) {
        bookingEngineAbstract.changeDatesOnBooking(bookingId, start, end);
    }

    @Override
    public void changeBookingItemOnBooking(String booking, String item) {
        bookingEngineAbstract.changeBookingItemOnBooking(booking, item);
    }

    @Override
    public void changeBookingItemAndDateOnBooking(String booking, String item, Date start, Date end) {
        bookingEngineAbstract.changeBookingItemAndDateOnBooking(booking, item,start,end);
    }

    public boolean isConfirmationRequired() {
        return bookingEngineAbstract.isConfirmationRequired();
    }

    public boolean canAdd(List<Booking> bookingsToAdd) {
        return bookingEngineAbstract.canAdd(bookingsToAdd);
    }

    public BookingGroup addBookings(List<Booking> bookingsToAdd) {
        return deepClone(bookingEngineAbstract.addBookings(bookingsToAdd));
    }

    @Override
    public Booking getBooking(String bookingId) {
        return deepClone(bookingEngineAbstract.getBooking(bookingId));
    }

    @Override
    public boolean  deleteBooking(String id) {
        return bookingEngineAbstract.deleteBooking(id);
    }

    @Override
    public BookingItemType getBookingItemType(String id) {
        return deepClone(bookingEngineAbstract.getBookingItemType(id));
    }

    public void addAvailability(String bookingItemId, Availability availability) {
        bookingEngineAbstract.addAvailability(bookingItemId, availability);
    }

    public Availability getAvailbility(String id) {
        return deepClone(bookingEngineAbstract.getAvailbility(id));
    }

    public List<Booking> getConfirmationList(String bookingItemTypeId) {
        return deepClone(bookingEngineAbstract.getConfirmationList(bookingItemTypeId));
    }
    
    public void confirmBooking(String bookingItemTypeId) {
        bookingEngineAbstract.confirmBooking(bookingItemTypeId);
    }

    public BookingTimeLineFlatten getTimelines(String id, Date startDate, Date endDate) {
        return deepClone(bookingEngineAbstract.getTimelines(id, startDate, endDate));
    }

    @Override
    public void deleteBookingItemType(String id) {
        bookingEngineAbstract.deleteBookingItemType(id);
    }

    /**
     * @todo Make unit tests for this.
     * @param start
     * @param end
     * @param itemId
     * @return 
     */
    public BookingTimeLineFlatten getTimeLinesForItem(Date start, Date end, String itemId) {
        return deepClone(bookingEngineAbstract.getTimeLinesForItem(start,end,itemId));
    }
    
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimal(Date start, Date end) {
        return deepClone(bookingEngineAbstract.getTimeLinesForItemWithOptimal(start,end, false));
    }
    
    @Override
    public List<BookingTimeLine> getTimelinesDirect(Date start, Date end, String itemTypeId) {
        BookingTimeLineFlatten res = getTimelines(itemTypeId, start, end);
        return res.getTimelines();
    }

    public boolean hasBookingsStartingBetweenTime(Date start, Date end, String itemId) {
        return true;
    }

    public boolean itemInUseBetweenTime(Date start, Date end, String itemId) {
        return bookingEngineAbstract.itemInUseBetweenTime(start, end, itemId);
    }

    /**
     * @TODO Add unit tests to this, and make sure it returns number of available items for the given type in the given time periode.
     * @param itemType
     * @param start
     * @param end
     * @return 
     */
    @Override
    public Integer getNumberOfAvailable(String itemType, Date start, Date end) {
        return bookingEngineAbstract.getAvailbleItems(itemType, start, end).size();
    }

    /**
     * @WARNING do not use this unless you are 100% sure about what you do. This is not secure and might fail.
     * Use getNumberOfAvailable instead. This will fail if you use it for more than one day at the time due to the shuffling problem.
     * @param itemType
     * @param start
     * @param end
     * @return 
     */
    public Integer getNumberOfAvailableWeakButFaster(String itemType, Date start, Date end) {
        return bookingEngineAbstract.getAvailbleItems(itemType, start, end).size();
    }

    /**
     * @TODO Make unit tests for this?
     * @return 
     */
    @Override
    public RegistrationRules getDefaultRegistrationRules() {
        return bookingEngineAbstract.getConfig().rules;
    }

     /**
     * @TODO Make unit tests for this?
     * @return 
     */
    @Override
    public void saveDefaultRegistrationRules(RegistrationRules rules) {
        bookingEngineAbstract.saveRules(rules);
    }

    @Override
    public void saveOpeningHours(TimeRepeaterData time, String itemId) {
        bookingEngineAbstract.saveOpeningHours(time, itemId);
    }

    @Override
    public List<TimeRepeaterData> getOpeningHoursWithType(String itemId, Integer timePeriodeType) {
        List<TimeRepeaterData> toReturn = new ArrayList();
        List<TimeRepeaterData> openingHours = deepClone(bookingEngineAbstract.getOpeningHours(itemId));
        for(TimeRepeaterData data : openingHours) {
            if(timePeriodeType == null || data.timePeriodeType.equals(timePeriodeType)) {
                toReturn.add(data);
            }
        }
        return toReturn;
    }
    
    @Override
    public List<TimeRepeaterData> getOpeningHours(String itemId) {
        return getOpeningHoursWithType(itemId, TimeRepeaterData.TimePeriodeType.open);
    }

    public List<BookingItem> getBookingItemsByType(String typeId) {
        return bookingEngineAbstract.getBookingItemsByType(typeId);
    }

    @Override
    public List<BookingItem> getAvailbleItems(String typeId, Date start, Date end) {
        return deepClone(bookingEngineAbstract.getAvailbleItems(typeId, start, end));
    }
    
    @Override
    public List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId) {
        return deepClone(bookingEngineAbstract.getAvailbleItemsWithBookingConsidered(typeId, start, end, bookingId));
    }

    @Override
    public List<BookingItem> getAllAvailbleItems(Date start, Date end) {
        return deepClone(bookingEngineAbstract.getAvailbleItems(start, end));
    }

    @Override
    public List<BookingItem> getAllAvailbleItemsWithBookingConsidered(Date start, Date end, String bookingid) {
        if (storeId.equals("178330ad-4b1d-4b08-a63d-cca9672ac329")) {
            List<BookingItem> res = bookingEngineAbstract.getAllAvailbleItemsWithBookingConsideredParalized(start, end, bookingid);
            return deepClone(res);
        }
        
        List<BookingItem> res = new ArrayList();
        List<BookingItemType> types = getBookingItemTypes();
        for(BookingItemType type : types) {
            List<BookingItem> found = getAvailbleItemsWithBookingConsidered(type.id, start, end, bookingid);
            res.addAll(found);
        }
        
        return deepClone(res);
    }

    @Override
    public void deleteOpeningHours(String repeaterId) {
        bookingEngineAbstract.deleteOpeningHours(repeaterId);
    }

    @Override
    public void checkConsistency() {
        bookingEngineAbstract.checkConsistency();
    }

    public boolean canAdd(Booking bookingToAdd) {
        return bookingEngineAbstract.canAdd(bookingToAdd);
    }

    public BookingItem getBookingItemUnfinalized(String id) {
        return deepClone(bookingEngineAbstract.getBookingItemUnfinalized(id));
    }

    @Override
    public void changeBookingItemType(String itemId, String newTypeId) {
        bookingEngineAbstract.changeBookingItemType(itemId, newTypeId);
    }

    @Override
    public void changeSourceOnBooking(String bookingId, String source) {
        bookingEngineAbstract.changeSourceOnBooking(bookingId, source);
    }

    @Override
    public void forceUnassignBookingInfuture() {
        bookingEngineAbstract.forceUnassignBookingInfuture();
    }
    
    /**
     * A fast coverage calculation routine.
     * Be aware that this result is cached and need to be manually resetted.
     * @param dateObject
     * @return 
     */
    public Integer getCoverageForDate(Date dateObject) {
        Date dateStart = new Date(dateObject.getTime() - (3600*1) * 1000);
        List<BookingItemType> types = getBookingItemTypes();
        Integer total = 0;
        Integer available = 0;
        for(BookingItemType type : types) {
            if(!type.visibleForBooking) {
                continue;
            }
            available += getNumberOfAvailableWeakButFaster(type.id, dateStart, dateObject);
            total += getBookingItemsByType(type.id).size();
        }
        
        Double res = ((double)(total - available) / (double)total) * 100;
        return res.intValue();
    }

    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimalIngoreErrors(Date start, Date end) {
        return deepClone(bookingEngineAbstract.getTimeLinesForItemWithOptimalIngoreErrors(start,end));
    }

    @Override
    public boolean canAddBooking(Booking bookingsToAdd) {
        return canAdd(bookingsToAdd);
    }
    
    @Override
    public boolean canAddBookings(List<Booking> bookingsToAdd) {
        return canAdd(bookingsToAdd);
    }
}