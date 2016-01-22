
package com.thundashop.core.bookingengine;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import java.util.Collections;
import java.util.Comparator;
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
        List<BookingItem> result = deepClone(bookingEngineAbstract.getBookingItems());
        Collections.sort(result, new Comparator<BookingItem>(){
            public int compare(BookingItem o1, BookingItem o2){
                if(o1.bookingItemName.equals(o2.bookingItemName))
                    return 0;
                return o1.bookingItemName.compareTo(o2.bookingItemName);
            }
       });
        return result;
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

    public void deleteBooking(String id) {
        bookingEngineAbstract.deleteBooking(id);
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
        return true;
    }

    public void autoAssignItem(String bookingId) {
        System.out.println("This need to give the booking a booking item");
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
        BookingTimeLineFlatten timeline = getTimelines(itemType, start, end);
        int higest = 9999;
        for(BookingTimeLine line : timeline.getTimelines()) {
            if(line.getAvailableSpots() < higest) {
                higest = line.getAvailableSpots();
            }
        }
        return higest;
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
    
}