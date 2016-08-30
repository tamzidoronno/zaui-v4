
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
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public boolean isConfirmationRequired() {
        return bookingEngineAbstract.isConfirmationRequired();
    }

    public boolean canAdd(List<Booking> bookingsToAdd) {
        return bookingEngineAbstract.canAdd(bookingsToAdd);
    }

    public BookingGroup addBookings(List<Booking> bookingsToAdd) {
        return deepClone(bookingEngineAbstract.addBookings(bookingsToAdd));
    }

    public Booking getBooking(String bookingId) {
        return deepClone(bookingEngineAbstract.getBooking(bookingId));
    }

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

    public boolean hasBookingsStartingBetweenTime(Date start, Date end, String itemId) {
        return true;
    }

    public boolean itemInUseBetweenTime(Date start, Date end, String itemId) {
        Booking booking = new Booking();
        booking.startDate = start;
        booking.endDate = end;
        booking.bookingItemId = itemId;
        booking.bookingItemTypeId = getBookingItem(itemId).bookingItemTypeId;
        
        List<Booking> toCheck = new ArrayList();
        toCheck.add(booking);
        boolean canAdd = canAdd(toCheck);
        return !canAdd;
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
        return bookingEngineAbstract.getNumberOfAvailable(itemType, start, end);
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
    public List<TimeRepeaterData> getOpeningHours(String itemId) {
        return deepClone(bookingEngineAbstract.getOpeningHours(itemId));
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
}