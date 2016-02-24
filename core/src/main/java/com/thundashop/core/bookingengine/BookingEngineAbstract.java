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
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingEngineAbstract extends GetShopSessionBeanNamed {
    
    @Autowired
    public PageManager pageManager;
    
    private final Map<String, Booking> bookings = new HashMap();
    private final Map<String, Availability> availabilities = new HashMap();
    private final Map<String, BookingItem> items = new HashMap();
    private final Map<String, BookingItemType> types = new HashMap();
    
    private BookingEngineConfiguration config = new BookingEngineConfiguration();
    
    private final BookingEngineVerifier verifier = new BookingEngineVerifier();

    public List<BookingItemType> getBookingItemTypes() {
        List<BookingItemType> result = new ArrayList(types.values());
        Comparator<BookingItemType> comparator = new Comparator<BookingItemType>() {
            public int compare(BookingItemType c1, BookingItemType c2) {
                return c1.name.compareTo(c2.name); // use your logic
            }
        };
        Collections.sort(result, comparator);
        return result;
    }
    
    public BookingItemType createABookingItemType(String name) {
        BookingItemType type = new BookingItemType();
        type.name = name;
        
        Page page = pageManager.createPageFromTemplatePage(getName()+"_bookingegine_type_template");
        type.pageId = page.id;
        
        saveObject(type);
        types.put(type.id, type);
        
        return type;
    }
    
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
        }
    }
    
    public Availability getAvailbility(String id) {
        return availabilities.get(id);  
    }
    
    public BookingItemType getBookingItemType(String bookingTypeId) {
        return types.get(bookingTypeId);
    }
    

    public BookingItem saveBookingItem(BookingItem item) {
        ensureNotOverwritingParameters(item);
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
        
        if (item.pageId.isEmpty()) {
            item.pageId = pageManager.createPageFromTemplatePage(getName()+"_bookingegine_item_template").id;
            saveObject(item);
        }
        
        for (String availabilityId : item.availabilitieIds) {
            item.availabilities.add(availabilities.get(availabilityId));
        }
        
        item.freeSpots = item.bookingSize - getAllBookingsByBookingItem(item.id).size();
        
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
        checkBookingItemIds(bookings);
        preProcessBookings(bookings);
        
        BookingGroup bookingGroup = new BookingGroup();
        if (getSession() != null && getSession().currentUser != null) {
            bookingGroup.userCreatedByUserId = getSession().currentUser.id;
        }
        
        for (Booking booking : bookings) {
            saveObject(booking);
            bookingGroup.bookingIds.add(booking.id);
        
            if (config.confirmationRequired) {
                booking.needConfirmation = true;
                saveObject(booking);
            } else {
                BookingItem bookingItem = items.get(booking.bookingItemId);
                bookingItem.bookingIds.add(booking.id);
                saveObject(bookingItem);
            }
            
            this.bookings.put(booking.id, booking);
        }
        
        
        saveObject(bookingGroup);
        return bookingGroup;
    }
    
    private void preProcessBookings(List<Booking> bookings) {
        validateBookings(bookings);
        checkIfCanAddBookings(bookings);
        checkIfAssigningPossible(bookings);
    }

    private void validateBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
        
            verifier.checkIfBookingItemIdExists(booking, types);
            
            if (!config.confirmationRequired) {
                verifier.throwExceptionIfBookingItemIdMissing(booking, items);
            }            
        }
    }

    public Booking getBooking(String id) {
        return bookings.get(id);
    }
    
    public List<Booking> getConfirmationList(String bookingItemTypeId) {
        return bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(bookingItemTypeId))
                .filter(booking -> booking.needConfirmation)
                .collect(Collectors.toList());
        
    }

    private void checkIfCanAddBookings(List<Booking> bookings) {
        List<Booking> checkedBookings = new ArrayList(); 
        
        for (Booking booking : bookings) {
            String bookingItemTypeId = booking.bookingItemTypeId;
            
            List<Booking> bookingsToConsider = this.bookings.values().stream()
                    .filter(o -> o.bookingItemTypeId.equals(bookingItemTypeId))
                    .filter(o -> o.interCepts(booking.startDate, booking.endDate))
                    .collect(Collectors.toList());
        
            if (booking.id == null || !booking.id.isEmpty()) {
                bookingsToConsider.removeIf(o -> o.id.equals(booking.id));
            }
            
            List<Booking> oldBookingsToConsider = checkedBookings.stream()
                    .filter(o -> o.bookingItemTypeId.equals(bookingItemTypeId))
                    .filter(o -> o.interCepts(booking.startDate, booking.endDate))
                    .collect(Collectors.toList());
            
            bookingsToConsider.addAll(oldBookingsToConsider);
            BookingTimeLineFlatten flattenTimeLine = getFlattenTimelinesFromBooking(booking);
            bookingsToConsider.stream().forEach(o -> flattenTimeLine.add(o));
            
            if (!flattenTimeLine.canAdd(booking)) {
                throw new BookingEngineException("There is no space for this booking, " + booking.getInformation());
            }
           
            checkedBookings.add(booking);
        }
    }

    private int getTotalSpotsForBookingItemType(String bookingItemTypeId) {
        return items.values().stream()
                .filter(item -> item.bookingItemTypeId.equals(bookingItemTypeId))
                .mapToInt(item -> item.bookingSize)
                .sum();
    }

    /**
     * Returns a list of availability 
     * within a given timePeriode
     * 
     * @param bookingItemTypeId
     * @param start
     * @param end
     * @return 
     */
    public BookingTimeLineFlatten getTimelines(String bookingItemTypeId, Date start, Date end) {
        int totalAvailble = getTotalSpotsForBookingItemType(bookingItemTypeId);
        BookingTimeLineFlatten flatten = new BookingTimeLineFlatten(totalAvailble, bookingItemTypeId);
        
        bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(bookingItemTypeId))
                .filter(booking -> booking.interCepts(start, end))
                .forEach(o -> flatten.add(o));
        
        flatten.start = start;
        flatten.end = end;
        return flatten;
    } 

    public BookingItemType updateBookingItemType(BookingItemType type) {
        BookingItemType savedItem = getBookingItemType(type.id);
        if (savedItem == null) {
            throw new BookingEngineException("Could not update itemType, it does not exists. Use createBookingItemType to make a new one");
        }
        
        savedItem.size = type.size;
        savedItem.name = type.name;
        savedItem.productId = type.productId;
        savedItem.pageId = type.pageId;
        savedItem.visibleForBooking = type.visibleForBooking;
        savedItem.addon = type.addon;
        savedItem.rules = type.rules;
        savedItem.order = type.order;
        savedItem.capacity = type.capacity;
        savedItem.description = type.description;
        saveObject(savedItem);
        return savedItem;
    }

    public List<BookingItem> getBookingItems() {
        List<BookingItem> list = new ArrayList(items.values());
        Comparator<BookingItem> comparator = new Comparator<BookingItem>() {
            public int compare(BookingItem c1, BookingItem c2) {
                return c1.bookingItemName.compareTo(c2.bookingItemName); // use your logic
            }
        };
        
        Collections.sort(list, comparator);
        return list;
    }

    public BookingEngineConfiguration getConfig() {
        return config;
    }

    public boolean canAdd(List<Booking> bookingsToAdd) {
        try {
            preProcessBookings(bookingsToAdd);
        } catch (BookingEngineException exception) {
            return false;
        }

        return true;
    }

    public List<Booking> getAllBookings() {
        ArrayList result = new ArrayList(bookings.values());
        return result;
    }

    public void deleteBooking(String id) {
        Booking booking = getBooking(id);
        bookings.remove(id);
        deleteObject(booking);
        
        for (BookingItem item : items.values()) {
            boolean removed = item.bookingIds.removeIf(o -> o.equals(booking.id));
            if (removed)
                saveObject(item);
        }
    }

    public void deleteBookingItem(String id) {
        BookingItem bookingItem = getBookingItem(id);
        if (bookingItem == null) {
            throw new BookingEngineException("Can not delete a booking that does not exists");
        }
        
        long count = bookings.values().stream()
                .filter(o -> o.bookingItemId != null && o.bookingItemId.equals(id))
                .count();
        
        if (count > 0) {
            throw new BookingEngineException("Can not delete a bookingItem when there is bookings connected to it");
        }
        
        BookingItem deleted = items.remove(id);
        databaseSaver.deleteObject(deleted, credentials);
    }
    
    public void changeTypeOnBooking(String bookingId, String itemTypeId) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            throw new BookingEngineException("Can not change type on a booking that does not exists");
        }
        
        BookingItemType type = getBookingItemType(itemTypeId);
        if (type == null) {
            throw new BookingEngineException("The type you tried to change to does not exists");
        }
        
        if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
            throw new BookingEngineException("Can not change BookingItemType on booking that already is assigned to a bookingItem");
        }
        
        Booking bookingClone = deepClone(booking);
        bookingClone.bookingItemTypeId = itemTypeId;
        validateChange(bookingClone);
        
        booking.bookingItemTypeId = itemTypeId;
        saveObject(booking);
    }

    private void validateChange(Booking bookingClone) {
        List<Booking> oldBookings = new ArrayList();
        oldBookings.add(bookingClone);
        preProcessBookings(oldBookings);
    }

    private BookingTimeLineFlatten getFlattenTimelinesFromBooking(Booking booking) {
        String bookingItemTypeId = booking.bookingItemTypeId;
        int totalSpots = getTotalSpotsForBookingItemType(bookingItemTypeId);
        return new BookingTimeLineFlatten(totalSpots, bookingItemTypeId);
    }

    private void checkBookingItemIds(List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.id != null &&  !booking.id.isEmpty()) {
                throw new BookingEngineException("Use saveBooking to update old bookings.");
            }
        }
    }
    
    public void changeDatesOnBooking(String bookingId, Date start, Date end) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            throw new BookingEngineException("Can not change dates on a booking that does not exists");
        }
        
        Booking newBooking = deepClone(booking);
        newBooking.startDate = start;
        newBooking.endDate = end;
        
        validateChange(newBooking);
        
        booking.startDate = start;
        booking.endDate = end;
        
        saveObject(booking);
    }

    public void changeBookingItemOnBooking(String bookingId, String itemId) {
        Booking booking = getBooking(bookingId);
        
        if (booking == null) {
            throw new BookingEngineException("Can not change bookingitem, the booking does not exists");
        }
        
        BookingItem bookingItem = getBookingItem(itemId);
        if (bookingItem == null) {
            throw new BookingEngineException("Can not change to a bookingItem that does not exists");
        }
        
        Booking newBooking = deepClone(booking);
        newBooking.bookingItemId = itemId;
        newBooking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        validateChange(newBooking);
        
        booking.bookingItemId = itemId;
        booking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        saveObject(booking);
    }

    public void deleteBookingItemType(String id) {
        BookingItemType type = types.get(id);
        if (type != null) {
            
            long count = items.values().stream().filter( o -> o.bookingItemTypeId.equals(id)).count();
            
            if (count > 0) {
                throw new BookingEngineException("Can not delete a bookingitemtype that already has booking items, Existing items: " + count);
            }
            types.remove(id);
            deleteObject(type);
        }
    }

    private void checkIfAssigningPossible(List<Booking> bookings) {
        Map<String, List<Booking>> groupedBookings = bookings.stream()
                .collect(Collectors.groupingBy(o -> o.bookingItemTypeId, Collectors.toList()));
        
        for (String bookingTypeId : groupedBookings.keySet()) {
            BookingItemType type = getBookingItemType(bookingTypeId);
            if (type == null) {
                throw new BookingEngineException("Did not find the booking item type for the booking");
            }
            
            List<Booking> checkBookings = groupedBookings.get(bookingTypeId);
            checkBookings.addAll(getBookingsNotAssigned(type.id));
            checkBookings.addAll(getAssignedBookingsAfterFirstBooking(type.id, bookings));
            
            removeFromBooking(checkBookings, bookings);
            for (Booking iBooking : bookings) {
                if (iBooking.id != null && !iBooking.id.isEmpty()) {
                    checkBookings.addAll(bookings);
                }
            }
            
            BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type, checkBookings, getItemsByType(type.id));
            
            // This throws exception if not possible.
            assigner.canAssign();
        }
    }

    private List<BookingItem> getItemsByType(String typeId) {
        return items.values().stream()
                .filter(o -> o.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
    }

    private List<Booking> getBookingsNotAssigned(String typeId) {
        return bookings.values().stream()
                .filter(o -> o.bookingItemId == null || o.bookingItemId.isEmpty())
                .filter(o -> o.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
    }

    private List<Booking> getAssignedBookingsAfterFirstBooking(String typeId, List<Booking> bookings) {
        List<Booking> sortedBookings = new ArrayList(bookings);
        Collections.sort(sortedBookings);
        
        if (sortedBookings.isEmpty()) {
            return new ArrayList();
        }

        return this.bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .filter(booking -> booking.bookingItemId != null && !booking.bookingItemId.isEmpty())
                .filter(booking -> booking.within(bookings.get(0).startDate, bookings.get(0).endDate))
                .collect(Collectors.toList()); 
    }

    BookingTimeLineFlatten getTimeLinesForItem(Date start, Date end, String itemId) {
        BookingItem item = getBookingItem(itemId);
        BookingTimeLineFlatten line = new BookingTimeLineFlatten(item.bookingSize, item.bookingItemTypeId);
        line.start = start;
        line.end = end;
        bookings.values().stream().
                filter(o -> (o.bookingItemId != null && o.bookingItemId.equals(itemId))).
                forEach(o -> line.add(o));
        return line;
   }

    void saveRules(RegistrationRules rules) {
        config.rules = rules;
        saveObject(config);
    }

    private void removeFromBooking(List<Booking> checkBookings, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if ( booking.id == null || booking.id.equals("")) {
                continue;
            }
            
            checkBookings.removeIf(ibooking -> ibooking.id.equals(booking.id));
        }
    }

    public List<TimeRepeaterData> getOpeningHours(String typeId) {
        List<TimeRepeaterData> result = new ArrayList();
        if(typeId == null || typeId.isEmpty()) {
            result = new ArrayList(config.openingHoursData.values());
        } else {
            BookingItemType bookingtype = getBookingItemType(typeId);
            if(bookingtype != null) {
               result = new ArrayList(bookingtype.openingHoursData.values());
            }
        }
        
        return result;
        
    }
    
    void saveOpeningHours(TimeRepeaterData time, String typeId) {
        if(time == null) {
            time = new TimeRepeaterData();
        }
        if(typeId == null || typeId.isEmpty()) {
            config.openingHoursData.put(time.repeaterId, time);
            saveObject(config);
        } else {
            BookingItemType type = getBookingItemType(typeId);
            type.openingHoursData.put(time.repeaterId, time);
            saveObject(type);
        }
    }
 
    public Integer getNumberOfAvailable(String itemType, Date start, Date end) {
        BookingTimeLineFlatten timeline = getTimelines(itemType, start, end);
        int higest = 9999;
        List<BookingTimeLine> timeLines = timeline.getTimelines();
        
        if (timeLines.isEmpty()) {
            return getTotalSpotsForBookingItemType(itemType);
        }
        
        for(BookingTimeLine line : timeLines) {
            if(line.getAvailableSpots() < higest) {
                higest = line.getAvailableSpots();
            }
        }
        return higest;
    }

    public List<BookingItem> getBookingItemsByType(String typeId) {
        return items.values().stream()
                .filter(o -> o.bookingItemTypeId != null && o.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
    }

    List<BookingItem> getAvailbleItems(Date start, Date end) {
        List<String> typeIds = new ArrayList();
        for(BookingItemType type : getBookingItemTypes()) {
            typeIds.add(type.id);
        }

        List<BookingItem> res = new ArrayList();
        for(String typeId : typeIds) {
            res.addAll(getAvailbleItems(typeId, start, end));
        }

        Collections.sort(res, new Comparator<BookingItem>(){
            public int compare(BookingItem o1, BookingItem o2){
                return o1.bookingItemName.compareTo(o2.bookingItemName);
            }
       });

        return res;
    }
    
    
    List<BookingItem> getAvailbleItems(String typeId, Date start, Date end) {
        BookingItemType type = types.get(typeId);
        if (type == null) {
            throw new BookingEngineException("Can not get available items ");
        }

        List<Booking> bookingsWithinDaterange = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .filter(booking -> booking.interCepts(start, end))
                .collect(Collectors.toList());

        List<BookingItem> bookingItems = getBookingItemsByType(typeId);

        BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type, bookingsWithinDaterange, bookingItems);

        return assigner.getAvailableItems().stream()
                .map(o -> items.get(o))
                .collect(Collectors.toList());
    }
    
    List<Booking> getAllBookingsByBookingItem(String bookingItemId) {
        return bookings.values().stream()
                .filter(booking -> booking.bookingItemId != null && booking.bookingItemId.equals(bookingItemId))
                .collect(Collectors.toList());
            
    }

    private void ensureNotOverwritingParameters(BookingItem item) {
        BookingItem inMemory = items.get(item.id);
        if (inMemory != null) {
            item.bookingIds = inMemory.bookingIds;
            item.waitingListBookingIds = inMemory.waitingListBookingIds;
        }
    }

    void deleteOpeningHours(String repeaterId) {
        List<BookingItemType> allTypes = getBookingItemTypes();
        for(BookingItemType type :allTypes) {
            if(type.openingHoursData.containsKey(repeaterId)) {
                type.openingHoursData.remove(repeaterId);
                updateBookingItemType(type);
            }
        }
        
        config.openingHoursData.remove(repeaterId);
        saveObject(config);
    }
}
