/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.app.contentmanager.data.ContentData;
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
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    
    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public UserManager userManager;
    
    private final Map<String, Booking> bookings = new HashMap();
    private final Map<String, Availability> availabilities = new HashMap();
    private final Map<String, BookingItem> items = new HashMap();
    private Map<String, BookingItemType> types = new HashMap();
    
    private BookingEngineConfiguration config = new BookingEngineConfiguration();
    
    private final BookingEngineVerifier verifier = new BookingEngineVerifier();

    public List<BookingItemType> getBookingItemTypes() {
        List<BookingItemType> result = new ArrayList(types.values());
        Comparator<BookingItemType> comparator = new Comparator<BookingItemType>() {
            public int compare(BookingItemType c1, BookingItemType c2) {
                if(c1.name == null || c2.name == null) {
                    return 0;
                } 
                return c1.name.compareTo(c2.name); // use your logic
            }
        };
        Collections.sort(result, comparator);
        result.stream().forEach(o -> finalize(o));
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
        
        updateBookingTypesIfTypeChanged();
        createScheduler("pmsprocessor", "0 6,16 * * *", CheckConsistencyCron.class);
    }
    
    public Availability getAvailbility(String id) {
        return availabilities.get(id);  
    }
    
    public BookingItemType getBookingItemType(String bookingTypeId) {
        return types.get(bookingTypeId);
    }
    
    public void changeBookingItemType(String itemId, String newTypeId) {
        BookingItem item = items.get(itemId);
        if (item == null)
            throw new BookingEngineException("Bookingitem you are trying to change does not exists");
        
        BookingItemType type = types.get(newTypeId);
        if (type == null) {
            throw new BookingEngineException("BookingitemType you are trying to change does not exists");
        }
        
        List<Booking> assignedBookings = assignAllBookingsThatHasType(item.bookingItemTypeId);
        
        List<Booking> bookingsWithType = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(item.bookingItemTypeId))
                .collect(Collectors.toList());
        
        for (Booking booking : bookingsWithType) {
            if (booking.bookingItemId != null && booking.bookingItemId.equals(itemId)) {
                booking.bookingItemTypeId = newTypeId;
                saveObject(booking);
            }
        }
        
        item.bookingItemTypeId = newTypeId;
        saveObject(item);
                
        unassignBookings(assignedBookings);
    }
    
    public BookingItem saveBookingItem(BookingItem item) {
        ensureNotOverwritingParameters(item);
        validate(item);
        updateBookingTypesIfTypeChanged();
        ensureItemIsValidWithAllBookings(item);
        saveObject(item);
        items.put(item.id, item);
        return finalize(item);
    }

    private void ensureItemIsValidWithAllBookings(BookingItem item) {
        if (item.id != null && !item.id.isEmpty()) {
            BookingItem inMemoryItem = items.get(item.id);
            items.put(item.id, item);
            try {
                checkAllBookings();
            } catch (Exception ex) {
                if (inMemoryItem != null) {
                    items.put(inMemoryItem.id, inMemoryItem);
                }
                updateBookingTypesIfTypeChanged();
                throw ex;
            }
        }
    }

    public BookingItem getBookingItem(String id) {
        return finalize(items.get(id));
    }

    public BookingItem getBookingItemUnfinalized(String id) {
        return items.get(id);
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
            if (iAvailbility.startDate.before(availability.startDate) && iAvailbility.endDate.after(availability.startDate) && shouldThrowException()) {
                throw new BookingEngineException("The availability overlaps in the beginning of another availbility");
            }
            
            if (availability.startDate.before(iAvailbility.endDate) && availability.startDate.after(iAvailbility.startDate) && shouldThrowException()) {
                throw new BookingEngineException("The availability overlaps in the end of another availbility");
            }
            
            if (availability.startDate.before(iAvailbility.startDate) && availability.endDate.after(iAvailbility.endDate) && shouldThrowException()) {
                throw new BookingEngineException("The availability overlaps a whole periode");
            }
            
            if (availability.startDate.equals(iAvailbility.startDate) && availability.endDate.equals(iAvailbility.endDate) && shouldThrowException()) {
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
        item.isFull = item.freeSpots < 1;
        
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
        checkIfAvailableBookingItemsOnlyEmptyBookings(bookings);
    }

    private void validateBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
        
            verifier.checkIfBookingItemIdExists(booking, types);
            
            if (!config.confirmationRequired) {
                verifier.throwExceptionIfBookingItemIdMissing(booking, items);
            }   
            
            if (booking.startDate != null && booking.endDate != null && booking.startDate.after(booking.endDate) && shouldThrowException()) {
                throw new BookingEngineException("Startdate can not be after enddate");
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
            
            if (!flattenTimeLine.canAdd(booking) && shouldThrowException()) {
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
            if (type != null && type.id != null && !type.id.isEmpty()) {
                types.put(type.id, type);
                saveObject(type);
                savedItem = type;
            } else {
                throw new BookingEngineException("Could not update itemType, it does not exists. Use createBookingItemType to make a new one");
            }
        }
        
        savedItem.size = type.size;
        savedItem.name = type.name;
        savedItem.pageId = type.pageId;
        savedItem.productId = type.productId;
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

    public boolean deleteBooking(String id) {
        Booking booking = getBooking(id);
        bookings.remove(id);
        if(booking == null) {
            return false;
        }
        deleteObject(booking);
        
        for (BookingItem item : items.values()) {
            boolean removed = item.bookingIds.removeIf(o -> o.equals(booking.id));
            if (removed) {
                saveObject(item);
                return true;
            }
        }
        
        return false;
    }

    public void deleteBookingItem(String id) {
        BookingItem bookingItem = getBookingItem(id);
        if (bookingItem == null) {
            throw new BookingEngineException("Can not delete a booking that does not exists");
        }
        
        long count = bookings.values().stream()
                .filter(o -> o.bookingItemId != null && o.bookingItemId.equals(id))
                .count();
        
        if (count > 0 && shouldThrowException()) {
            throw new BookingEngineException("Can not delete a bookingItem when there is bookings connected to it");
        }
        
        BookingItem deleted = items.remove(id);
        deleteObject(deleted);
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
        
        if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty() && shouldThrowException()) {
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
            if (booking.id != null &&  !booking.id.isEmpty() && shouldThrowException()) {
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
        if (bookingItem == null && !itemId.isEmpty()) {
            throw new BookingEngineException("Can not change to a bookingItem that does not exists");
        }
        
        Booking newBooking = deepClone(booking);
        newBooking.bookingItemId = itemId;
        if (bookingItem != null)
            newBooking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        
        validateChange(newBooking);
        
        booking.bookingItemId = itemId;
        if (bookingItem != null)
            booking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        
        saveObject(booking);
    }
    
    public void changeBookingItemAndDateOnBooking(String bookingId, String itemId, Date start, Date end) {
        Booking booking = getBooking(bookingId);
        
        if (booking == null) {
            throw new BookingEngineException("Can not change bookingitem, the booking does not exists");
        }
        
        BookingItem bookingItem = getBookingItem(itemId);
        if (bookingItem == null && !itemId.isEmpty()) {
            throw new BookingEngineException("Can not change to a bookingItem that does not exists");
        }
        
        Booking newBooking = deepClone(booking);
        newBooking.bookingItemId = itemId;
        if (bookingItem != null)
            newBooking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        
        newBooking.startDate = start;
        newBooking.endDate = end;
        newBooking.bookingItemId = itemId;
        
        validateChange(newBooking);
        
        booking.bookingItemId = itemId;
        booking.startDate = start;
        booking.endDate = end;
        if (bookingItem != null) {
            booking.bookingItemTypeId = bookingItem.bookingItemTypeId;
        }
        
        
        saveObject(booking);
    }
    

    public void deleteBookingItemType(String id) {
        BookingItemType type = types.get(id);
        if (type != null) {
            
            List<BookingItem> itemInUse = items.values().stream().filter( o -> o.bookingItemTypeId.equals(id)).collect(Collectors.toList());
            long count = itemInUse.size();
            
            if (count > 0 && shouldThrowException()) {
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
            
            BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type, checkBookings, getItemsByType(type.id), shouldThrowException());
            
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
                filter(o -> o.within(start, end)).
                filter(o -> (o.bookingItemId != null && o.bookingItemId.equals(itemId))).
                forEach(o -> line.add(o));
        return line;
    }
   
    BookingTimeLineFlatten getTimeLinesForItemWithOptimal(Date start, Date end, String itemId) {
        BookingItem item = getBookingItem(itemId);
        BookingTimeLineFlatten line = new BookingTimeLineFlatten(item.bookingSize, item.bookingItemTypeId);
        line.start = start;
        line.end = end;
        bookings.values().stream().
                filter(o -> o.within(start, end)).
                filter(o -> (o.bookingItemId != null && o.bookingItemId.equals(itemId))).
                forEach(o -> line.add(o));
        
        
        BookingItemAssignerOptimal assigner = getAvailableItemsAssigner(item.bookingItemTypeId, start, end, null);
        List<String> autoAssignedBookings = assigner.getBookingsFromTimeLine(itemId);
        
        autoAssignedBookings.stream()
                .map(o -> bookings.get(o))
                .filter(o -> !line.containsBooking(o))
                .forEach(o -> line.add(o));
        
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
        if (itemType.equals("aee14a7f-dc19-475c-91ee-5d3310475887")) {
            System.out.println("Found one");
        }
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
        return getAvailbleItemsWithBookingConsidered(typeId, start, end, null);
    }
    
    List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId) {
        BookingItemAssignerOptimal assigner = getAvailableItemsAssigner(typeId, start, end, null);

        List<BookingItem> retList = assigner.getAvailableItems(bookingId, start, end).stream()
                .map(o -> items.get(o))
                .collect(Collectors.toList());
        
        return retList;
    }

    private BookingItemAssignerOptimal getAvailableItemsAssigner(String typeId, Date start, Date end, String bookingId) throws BookingEngineException {

        BookingItemType type = types.get(typeId);
        if (type == null) {
            throw new BookingEngineException("Can not get available items ");
        }
        
        Set<Booking> bookingsWithinDaterange = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .filter(booking -> booking.interCepts(start, end))
                .collect(Collectors.toSet());
        
        List<Booking> checkIt = new ArrayList(bookingsWithinDaterange);
        for (Booking ibooking : checkIt) {
            List<Booking> overlapping = bookings.values().stream()
                    .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                    .filter(booking -> booking.interCepts(ibooking.startDate, ibooking.endDate))
                    .collect(Collectors.toList());
            
            bookingsWithinDaterange.addAll(overlapping);
        }
        
        if (bookingId != null && !bookingId.isEmpty()) {
            bookingsWithinDaterange.removeIf(o -> o.id.equals(bookingId));
        }
        
        List<BookingItem> bookingItems = getBookingItemsByType(typeId);
        BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type, new ArrayList(bookingsWithinDaterange), bookingItems, shouldThrowException());
        return assigner;
    }
    
    List<Booking> getAllBookingsByBookingItem(String bookingItemId) {
        return bookings.values().stream()
                .filter(booking -> booking.bookingItemId != null && booking.bookingItemId.equals(bookingItemId))
                .collect(Collectors.toList());
            
    }

    private void ensureNotOverwritingParameters(BookingItem item) {
        
        if (item == null) {
            logPrint("What?");
        }
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

    private void updateBookingTypesIfTypeChanged() {
        for (Booking booking : bookings.values()) {
            if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
                BookingItem item = items.get(booking.bookingItemId);
                if (!item.bookingItemTypeId.equals(booking.bookingItemTypeId)) {
                    logPrint("Updating: " + item.bookingItemName);
                    booking.bookingItemTypeId = item.bookingItemTypeId;
                    saveObject(booking);
                }
            }
        }
    }
    
    public void checkConsistency() {
        try {
            checkAllBookings();
        } catch (Exception x) {
            messageManager.sendErrorNotification(x.getMessage(), x);
        }
    }

    private void checkAllBookings() {
        for (BookingItemType type : types.values()) {
            List<BookingItem> bookingItems = getBookingItemsByType(type.id);
            List<Booking> toCheck = bookings.values()
                    .stream()
                    .filter(o -> o.bookingItemTypeId != null && o.bookingItemTypeId.equals(type.id))
                    .collect(Collectors.toList());
            
            new BookingItemAssignerOptimal(type, toCheck, bookingItems, shouldThrowException()).canAssign();
        }
    }

    private Boolean shouldThrowException() {
        if (getSession() != null && getSession().get("ignoreBookingErrors") != null && getSession().get("ignoreBookingErrors").equals("true")) {
            return false;
        }
        
        return true;
    }

    boolean canAdd(Booking bookingToAdd) {
        List<Booking> toCheck = new ArrayList();
        toCheck.add(bookingToAdd);
        return canAdd(toCheck);
    }
    
    public BookingGroup addBookingToWaitingList(String bookingItemId, List<Booking> bookings) {
        validateBookings(bookings);
        
        BookingItem bookingItem = getBookingItem(bookingItemId);
        
        if (bookingItem == null) {
            throw new BookingEngineException("Could not add booking to waitinglist, the bookingitem does not exists");
        }
        
        int newSize = bookings.size() + bookingItem.waitingListBookingIds.size();
        if (bookingItem.waitingListSize > newSize && shouldThrowException()) {
            throw new BookingEngineException("There is not enough space left to add user to waitinglist.");
        }
        
        BookingGroup bookingGroup = new BookingGroup();
        if (getSession() != null && getSession().currentUser != null) {
            bookingGroup.userCreatedByUserId = getSession().currentUser.id;
        }
        
        for (Booking booking : bookings) {
            booking.bookingItemId = bookingItemId;
            saveObject(booking);
            this.bookings.put(booking.id, booking);
            bookingGroup.bookingIds.add(booking.id);
            bookingItem.waitingListBookingIds.add(booking.id);
        }
        
        return bookingGroup;
    }
    
    public void removeBookingsWhereUserHasBeenDeleted(String bookingItemId) {
        List<Booking> bookings = getAllBookingsByBookingItem(bookingItemId);
        for (Booking booking : bookings) {
            if (!userManager.doesUserExsist(booking.userId)) {
                deleteBooking(booking.id);
            }
        }
    }

    private void finalize(BookingItemType o) {
        if (o.pageId == null || o.pageId.isEmpty()) {
            Page page = pageManager.createPageFromTemplatePage(getName()+"_bookingegine_type_template");
            o.pageId = page.id;
            saveObject(o);
        }
    }

    private void checkIfAvailableBookingItemsOnlyEmptyBookings(List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.id != null && !booking.id.isEmpty()) {
                
                BookingItemAssignerOptimal assigner = getAvailableItemsAssigner(booking.bookingItemTypeId, booking.startDate, booking.endDate, null);
                
                List<BookingItem> availableItems = assigner.getAvailableItems(booking.id, booking.startDate, booking.endDate).stream()
                .map(o -> items.get(o))
                .collect(Collectors.toList());
          
                if (booking.bookingItemId != null && !booking.bookingItemId.isEmpty()) {
                    boolean canUseRoom = availableItems.stream().map(item -> item.id)
                            .collect(Collectors.toList())
                            .contains(booking.bookingItemId);
                    
                    if (!canUseRoom) {
                        List<Booking> bookingsUsingItem = assigner.getBookingThatUseItem(booking.bookingItemId);
                        String extra = "";
                        int i = 0;
                        
                        for (Booking ibooking : bookingsUsingItem) {
                            if (ibooking.interCepts(booking.startDate, booking.endDate)) {
                                i++;
                                extra += "<br/> " + i + ". " + ibooking.getHumanReadableDates();
                            }
                        }
                        
                        if (i > 0 && shouldThrowException()) {
                            throw new BookingEngineException("This bookingitem can not be used as it is not available, there is other bookings that depends on this." + extra);
                        }
                    }
                }
            }
        }
    }

    private List<Booking> assignAllBookingsThatHasType(String typeId) {
        List<Booking> unassignedBookings = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .filter(booking -> booking.isUnassigned())
                .collect(Collectors.toList());
        
        for (Booking booking : unassignedBookings) {
            List<BookingItem> canUseItems = getAvailbleItemsWithBookingConsidered(booking.bookingItemTypeId, booking.startDate, booking.endDate, booking.id);
            changeBookingItemOnBooking(booking.id, canUseItems.get(0).id);
        }
        
        return unassignedBookings;
    }

    private void unassignBookings(List<Booking> assignedBookings) {
        assignedBookings.stream().forEach(booking -> changeBookingItemOnBooking(booking.id, ""));
    }
}
