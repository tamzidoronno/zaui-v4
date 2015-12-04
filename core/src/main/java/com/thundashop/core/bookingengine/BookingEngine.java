/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingRequiredConfirmationList;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingEngine extends GetShopSessionBeanNamed  {
    
    private final Map<String, Booking> bookings = new HashMap();
    private final Map<String, Availability> availabilities = new HashMap();
    private final Map<String, BookingItem> items = new HashMap();
    private final Map<String, BookingItemType> types = new HashMap();
    
    /**
     * The keys and values.
     * 
     * This list contains all the bookings that require a confirmation.
     * 
     * Key = bookingItemTypeId
     */
    private final Map<String, BookingRequiredConfirmationList> requiredConfirmation = new HashMap();
    
    private BookingEngineConfiguration config = new BookingEngineConfiguration();
    
    private final BookingEngineVerifier verifier = new BookingEngineVerifier();

    public List<BookingItemType> getBookingItemTypes() {
        return new ArrayList(types.values());
    }
    
    public BookingItemType createABookingItemType(String name) {
        BookingItemType type = new BookingItemType();
        type.name = name;
        saveObject(type);
        types.put(type.id, type);
        
        return type;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof BookingEngineConfiguration) {
                config = (BookingEngineConfiguration)dataCommon;
            }
            
            if (dataCommon instanceof Availability) {
                Availability av = (Availability)dataCommon;
                availabilities.put(av.id, av);
            }
            
            if (dataCommon instanceof BookingItem) {
                BookingItem item = (BookingItem)dataCommon;
                items.put(item.id, item);
            }
            
            if (dataCommon instanceof BookingItemType) {
                BookingItemType itemType = (BookingItemType)dataCommon;
                types.put(itemType.id, itemType);
            }
            
            if (dataCommon instanceof Booking) {
                Booking booking = (Booking)dataCommon;
                bookings.put(booking.id, booking);
            }
            
            if (dataCommon instanceof BookingRequiredConfirmationList) {
                BookingRequiredConfirmationList confirmationList = (BookingRequiredConfirmationList)dataCommon;
                requiredConfirmation.put(confirmationList.bookingItemTypeId, confirmationList);
            }
        }
    }
    
    public Availability getAvailbility(String id) {
        return availabilities.get(id);  
    }
    
    public BookingItemType getBookingItemType(String bookingTypeId) {
        return types.get(bookingTypeId);
    }
    

    public BookingItem saveBookingItem(BookingItem item) {
        validate(item);
        saveObject(item);
        items.put(item.id, item);
        return finalize(item);
    }

    public BookingItem getBookingItem(String id) {
        return finalize(items.get(id));
    }

    private void validate(BookingItem item) {
        BookingItemType type = types.get(item.bookingItemTypeId);
        if (type == null) {
            throw new BookingEngineException("Trying to save a BookingItem without a valid BookingItemType");
        }
    }

    public Availability addAvailability(String bookingItemId, Availability availability) {
        BookingItem item = getBookingItem(bookingItemId);
        
        validateAvailbility(item, availability);
        checkOverLappingAvailibility(item, availability);
        
        saveObject(availability);
        availabilities.put(availability.id, availability);
        
        item.availabilitieIds.add(availability.id);
        saveObject(item);
        
        return availability;
    }

    private void validateAvailbility(BookingItem item, Availability availability) throws BookingEngineException {
        if (item == null) {
            throw new BookingEngineException("Could not add availability, the bookingItem does not exists");
        }
        
        if (availability.startDate == null || availability.endDate == null) {
            throw new BookingEngineException("Availabilities without dates is not allowed!");
        }
    }

    private void checkOverLappingAvailibility(BookingItem item, Availability availability) {
        for (Availability iAvailbility : item.availabilities) {
            if (iAvailbility.startDate.before(availability.startDate) && iAvailbility.endDate.after(availability.startDate)) {
                throw new BookingEngineException("The availability overlaps in the beginning of another availbility");
            }
            
            if (availability.startDate.before(iAvailbility.endDate) && availability.startDate.after(iAvailbility.startDate)) {
                throw new BookingEngineException("The availability overlaps in the end of another availbility");
            }
            
            if (availability.startDate.before(iAvailbility.startDate) && availability.endDate.after(iAvailbility.endDate)) {
                throw new BookingEngineException("The availability overlaps a whole periode");
            }
            
            if (availability.startDate.equals(iAvailbility.startDate) && availability.endDate.equals(iAvailbility.endDate)) {
                throw new BookingEngineException("The availability overlaps exact same periode");
            }
        }
    }

    private BookingItem finalize(BookingItem item) {
        if (item == null) {
            return null;
        }
        
        item.availabilities = new ArrayList();
        
        for (String availabilityId : item.availabilitieIds) {
            item.availabilities.add(availabilities.get(availabilityId));
        }
        
        return item;
    }

    public boolean isConfirmationRequired() {
        return config.confirmationRequired;
    }

    public void setConfirmationRequired(boolean confirmationRequired ) {
        config.confirmationRequired = confirmationRequired;
        saveObject(config);
    }

    /**
     * Adds only new bookings to the system.
     * Changing old bookings should be done trough the "saveBooking" function
     * 
     * @param bookings
     * @return 
     */
    public BookingGroup addBookings(List<Booking> bookings) {
        validateBookings(bookings);
        checkIfCanAddBookings(bookings);
        
        BookingGroup bookingGroup = new BookingGroup();
        if (getSession() != null && getSession().currentUser != null) {
            bookingGroup.userCreatedByUserId = getSession().currentUser.id;
        }
        
        for (Booking booking : bookings) {
            saveObject(booking);
            bookingGroup.bookingItemIds.add(booking.id);
        
            if (config.confirmationRequired) {
                BookingRequiredConfirmationList list = getConfirmationListInternal(booking.bookingItemTypeId);
                list.bookings.add(booking.id);
                saveObject(list);
            } else {
                BookingItem bookingItem = items.get(booking.bookingItemId);
                bookingItem.bookingIds.add(booking.id);
                saveObject(bookingItem);
            }
        }
        
        
        saveObject(bookingGroup);
        return bookingGroup;
    }

    private void validateBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
            verifier.checkIfBookingItemIdExists(booking, types);
        
            if (!config.confirmationRequired) {
                verifier.throwExceptionIfBookingItemIdMissing(booking, items);
            }
            
            if (booking.id != null &&  !booking.id.isEmpty()) {
                throw new BookingEngineException("Use saveBooking to update old bookings.");
            }
        }
    }

    public Booking getBooking(String id) {
        return bookings.get(id);
    }

    private BookingRequiredConfirmationList getConfirmationListInternal(String bookingItemTypeId) {
       BookingRequiredConfirmationList bookingsThatRequireConfirmation = requiredConfirmation.get(bookingItemTypeId);
       
       if (bookingsThatRequireConfirmation == null) {
           bookingsThatRequireConfirmation = new BookingRequiredConfirmationList();
           bookingsThatRequireConfirmation.bookingItemTypeId = bookingItemTypeId;
           requiredConfirmation.put(bookingItemTypeId, bookingsThatRequireConfirmation);
       }
       
       return bookingsThatRequireConfirmation;
    }
    
    public BookingRequiredConfirmationList getConfirmationList(String bookingItemTypeId) {
       BookingRequiredConfirmationList bookingsThatRequireConfirmation = getConfirmationListInternal(bookingItemTypeId);
       return bookingsThatRequireConfirmation.jsonClone();
    }

    private void checkIfCanAddBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
            String bookingItemTypeId = booking.bookingItemTypeId;
            int totalSpots = getTotalSpotsForBookingItemType(bookingItemTypeId);
            List<Booking> bookingsToConsider = this.bookings.values().stream()
                    .filter(o -> o.bookingItemTypeId.equals(bookingItemTypeId))
                    .collect(Collectors.toList());
            
            BookingTimeLineFlatten flattenTimeLine = new BookingTimeLineFlatten(totalSpots);
            if (!flattenTimeLine.canAdd(booking)) {
                throw new BookingEngineException("There is no space for this booking");
            }
            
            bookingsToConsider.add(booking);
        }
    }

    private int getTotalSpotsForBookingItemType(String bookingItemTypeId) {
        return items.values().stream()
                .filter(item -> item.bookingItemTypeId.equals(bookingItemTypeId))
                .mapToInt(item -> item.bookingSize)
                .sum();
    }
}