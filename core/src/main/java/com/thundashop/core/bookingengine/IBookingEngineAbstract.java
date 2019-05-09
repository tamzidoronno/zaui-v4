/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public interface IBookingEngineAbstract {
    public BookingItemType createABookingItemType(String name);
    public List<BookingItemType> getBookingItemTypes();
    public List<BookingItemType> getBookingItemTypesWithSystemType(Integer systemType);
    public BookingItemType updateBookingItemType(BookingItemType type);
    public BookingItem saveBookingItem(BookingItem item);
    public BookingItem getBookingItem(String id);
    public List<BookingItem> getBookingItems();
    public void deleteBookingItem(String id);
    public BookingEngineConfiguration getConfig();
    public List<Booking> getAllBookings();
    public List<Booking> getAllBookingsByBookingItem(String bookingItemId);
    public void removeBookingsWhereUserHasBeenDeleted(String bookingItemId);
    public void changeTypeOnBooking(String bookingId, String itemTypeId);
    public void changeDatesOnBooking(String bookingId, Date start, Date end);
    public void changeBookingItemOnBooking(String bookingId, String itemId);
    public void changeBookingItemAndDateOnBooking(String bookingId, String itemId, Date start, Date end);
    public boolean isConfirmationRequired();
    public boolean canAdd(List<Booking> bookingsToAdd);
    public BookingGroup addBookings(List<Booking> bookings);
    public Booking getBooking(String id);
    public boolean deleteBooking(String id);
    public BookingItemType getBookingItemType(String bookingTypeId);
    public Availability addAvailability(String bookingItemId, Availability availability);
    public Availability getAvailbility(String id);
    public BookingTimeLineFlatten getTimelines(String bookingItemTypeId, Date start, Date end);
    public void deleteBookingItemType(String id);
    public BookingTimeLineFlatten getTimeLinesForItem(Date start, Date end, String itemId);
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimal(Date start, Date end, boolean ignoreErrors);
    public boolean itemInUseBetweenTime(Date start, Date end, String itemId);
    public List<BookingItem> getAvailbleItems(String typeId, Date start, Date end);
    public int getNumberOfPossibleBookings(String itemType, Date start, Date end);
    public Integer getNumberOfAvailable(String itemType, Date start, Date end);
    public void saveRules(RegistrationRules rules);
    public List<BookingItem> getBookingItemsByType(String typeId);
    public List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId);
    public List<BookingItem> getAvailbleItemsWithBookingConsideredAndShuffling(String typeId, Date start, Date end, String bookingId);
    public List<BookingItem> getAvailbleItems(Date start, Date end);
    public List<BookingItem> getAllAvailbleItemsWithBookingConsideredParalized(Date start, Date end, String bookingid);
    public void checkConsistency();
    public boolean canAdd(Booking bookingToAdd);
    public BookingItem getBookingItemUnfinalized(String id);
    public void changeBookingItemType(String itemId, String newTypeId);
    public void changeSourceOnBooking(String bookingId, String source);
    public void forceUnassignBookingInfuture();
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimalIngoreErrors(Date start, Date end);
    
    public void setConfirmationRequired(boolean confirmationRequired );
    public List<Booking> getConfirmationList(String bookingItemTypeId);
    public void confirmBooking(String bookingId);
    public void saveOpeningHours(TimeRepeaterData time, String typeId);
    public List<TimeRepeaterData> getOpeningHours(String typeId);
    public void deleteOpeningHours(String repeaterId);
    public Booking getActiveBookingOnBookingItem(String bookingItemId);

    public void changeDepartmentOnType(String bookingItemTypeId, String departmentId);

    public Integer getNumberOfAvailableExcludeClose(String itemType, Date start, Date end);
    public List<String> getBookingItemTypesIds();
}
