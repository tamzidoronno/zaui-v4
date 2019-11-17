/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import static com.thundashop.core.bookingengine.BookingEngine.useNewEngine;
import static com.thundashop.core.bookingengine.BookingEngineAbstract.usingNewSystem2;
import com.thundashop.core.bookingengine.data.Availability;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingGroup;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.department.Department;
import com.thundashop.core.department.DepartmentManager;
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
public class BookingEngineNew extends GetShopSessionBeanNamed implements IBookingEngineAbstract {
 
    @Autowired
    public PageManager pageManager;
    
    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public DepartmentManager departmentManager;
    
    private final Map<String, Booking> bookings = new HashMap();
    private final Map<String, BookingItem> items = new HashMap();
    private Map<String, BookingItemType> types = new HashMap();
    
    private BookingEngineConfiguration config = new BookingEngineConfiguration();
    
    private final BookingEngineVerifier verifier = new BookingEngineVerifier();

    private Date lastSentErrorNotification = new Date();

    public BookingEngineNew() {
        setOverrideCollectionName("BookingEngineAbstract");
    }
    
    @Override
    public List<String> getBookingItemTypesIds() {
        return types.values().stream()
                .map(type -> type.id)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookingItemType> getBookingItemTypes() {
        List<BookingItemType> normalBookingTypes = new ArrayList();
        normalBookingTypes.addAll(getBookingItemTypesWithSystemType(0));
        normalBookingTypes.addAll(getBookingItemTypesWithSystemType(3));
        return normalBookingTypes; 
    }
    
    @Override
    public List<BookingItemType> getBookingItemTypesWithSystemType(Integer systemType) {
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

        if(systemType == null) {
            return result;
        }
        
        List<BookingItemType> allItems = new ArrayList();
        for(BookingItemType type : result) {
            if(type.systemCategory != systemType) {
                continue;
            }
            allItems.add(type);
        }
        
        return allItems;
    }
    
    @Override
    public BookingItemType createABookingItemType(String name) {
        BookingItemType type = new BookingItemType();
        type.name = name;
        
        Page page = pageManager.createPageFromTemplatePage(getName()+"_bookingegine_type_template");
        type.pageId = page.id;
        
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
                booking.stripSeconds();
                bookings.put(booking.id, booking);
            }
        }
        
        updateBookingTypesIfTypeChanged();
        createScheduler("pmsprocessor", "0 6,16 * * *", CheckConsistencyCron.class);
    }
    
    @Override
    public Availability getAvailbility(String id) {
        throw new RuntimeException("Legacy code not in use");
    }
    
    @Override
    public BookingItemType getBookingItemType(String bookingTypeId) {
        return types.get(bookingTypeId);
    }
    
    @Override
    public void changeBookingItemType(String itemId, String newTypeId) {
        unassignAllFutureBookings();
        
        BookingItem item = items.get(itemId);
        if (item == null)
            throw new BookingEngineException("Bookingitem you are trying to change does not exists");
        
        BookingItemType type = types.get(newTypeId);
        if (type == null) {
            throw new BookingEngineException("BookingitemType you are trying to change does not exists");
        }
        
        assignAllBookingsThatHasType(item.bookingItemTypeId);
        
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
                
        unassignAllFutureBookings();
    }
    
    @Override
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
                validateBookings(new ArrayList());
            } catch (Exception ex) {
                if (inMemoryItem != null) {
                    items.put(inMemoryItem.id, inMemoryItem);
                }
                updateBookingTypesIfTypeChanged();
                throw ex;
            }
        }
    }

    @Override
    public BookingItem getBookingItem(String id) {
        return finalize(items.get(id));
    }

    @Override
    public BookingItem getBookingItemUnfinalized(String id) {
        return items.get(id);
    }

    private void validate(BookingItem item) {
        BookingItemType type = types.get(item.bookingItemTypeId);
        if (type == null) {
            throw new BookingEngineException("Trying to save a BookingItem without a valid BookingItemType");
        }
    }

    @Override
    public Availability addAvailability(String bookingItemId, Availability availability) {
        throw new RuntimeException("Legacy code not in use");
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
        
        item.freeSpots = item.bookingSize - getAllBookingsByBookingItem(item.id).size();
        item.isFull = item.freeSpots < 1;
        
        return item;
    }

    @Override
    public boolean isConfirmationRequired() {
        return config.confirmationRequired;
    }

    @Override
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
    @Override
    public BookingGroup addBookings(List<Booking> bookings) {
        checkBookingItemIds(bookings);
        
        validateBookings(bookings);
        
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
                
                if (bookingItem != null) {
                    bookingItem.bookingIds.add(booking.id);
                    saveObject(bookingItem);
                }
            }
            
            this.bookings.put(booking.id, booking);
        }
        
        
        saveObject(bookingGroup);
        return bookingGroup;
    }

    private void validateBooking(Booking booking) {
        List<Booking> toCheck = new ArrayList();
        toCheck.add(booking);
        validateBookings(toCheck);
    }
    
    private void validateBookings(List<Booking> bookings) {
        BookingValidator validator = new BookingValidator(items);
        
        this.bookings.values()
                .stream()
                .forEach(b -> validator.add(b));
        
        bookings.stream()
                .forEach(b -> validator.add(b));
        
        validator.validate();
    }

    @Override
    public Booking getBooking(String id) {
        return bookings.get(id);
    }
    
    @Override
    public List<Booking> getConfirmationList(String bookingItemTypeId) {
        return bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(bookingItemTypeId))
                .filter(booking -> booking.needConfirmation)
                .collect(Collectors.toList());
        
    }
    
    /**
     *
     * @param bookingId
     */
    @Override
    public void confirmBooking(String bookingId) {
        throw new RuntimeException("Legacy code not in use");
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
    @Override
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

    @Override
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
        savedItem.autoConfirm = type.autoConfirm;
        savedItem.addon = type.addon;
        savedItem.group = type.group;
        savedItem.rules = type.rules;
        savedItem.order = type.order;
        savedItem.capacity = type.capacity;
        savedItem.nameTranslations = type.nameTranslations;
        savedItem.descriptionTranslations = type.descriptionTranslations;
        savedItem.description = type.description;
        savedItem.eventItemGroup = type.eventItemGroup;
        savedItem.minStay = type.minStay;
        savedItem.systemCategory = type.systemCategory;
        savedItem.historicalProductIds = type.historicalProductIds;
        savedItem.setTranslationStrings(type.getTranslations());
        saveObject(savedItem);
        return savedItem;
    }

    @Override
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

    @Override
    public BookingEngineConfiguration getConfig() {
        return config;
    }

    @Override
    public boolean canAdd(List<Booking> bookingsToAdd) {
        try {
            validateBookings(bookingsToAdd);
        } catch (BookingEngineException exception) {
            return false;
        }

        return true;
    }

    @Override
    public List<Booking> getAllBookings() {
        ArrayList result = new ArrayList(bookings.values());
        return result;
    }


    @Override
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

    @Override
    public void deleteBookingItem(String id) {
        BookingItem bookingItem = getBookingItem(id);
        if (bookingItem == null) {
            throw new BookingEngineException("Can not delete a bookingitem that does not exists");
        }
        
        List<Booking> bookingsConnectedToItem = bookings.values().stream()
                .filter(o -> o.bookingItemId != null && o.bookingItemId.equals(id))
                .collect(Collectors.toList());
        
        
        if (!bookingsConnectedToItem.isEmpty() && shouldThrowException()) {
            throw new BookingEngineException("Can not delete a bookingItem when there is bookings connected to it");
        }
        
        BookingItem deleted = items.remove(id);
        deleteObject(deleted);
    }
    
    @Override
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
        
        String oldId = booking.bookingItemTypeId;
        try {
            booking.bookingItemTypeId = itemTypeId;
            validateBooking(booking);
            saveObject(booking);
        } catch (BookingEngineException ex) {
            booking.bookingItemTypeId = oldId;
            throw ex;
        }
    }

    private void checkBookingItemIds(List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (booking.id != null &&  !booking.id.isEmpty() && shouldThrowException()) {
                throw new BookingEngineException("Use saveBooking to update old bookings.");
            }
        }
    }
    
    @Override
    public void changeDatesOnBooking(String bookingId, Date start, Date end) {
        Booking booking = getBooking(bookingId);
        if (booking == null) {
            throw new BookingEngineException("Can not change dates on a booking that does not exists");
        }
        
        Date oldStartDate = booking.startDate;
        Date oldEndDate = booking.endDate;
        
        try {
            booking.startDate = start;
            booking.endDate = end;
            validateBooking(booking);
            saveObject(booking);
        } catch (BookingEngineException ex) {
            booking.startDate = oldStartDate;
            booking.endDate = oldEndDate;
            throw ex;
        }
    }

    @Override
    public void changeBookingItemOnBooking(String bookingId, String itemId) {
        Booking booking = getBooking(bookingId);
        
        if (booking == null) {
            throw new BookingEngineException("Can not change bookingitem, the booking does not exists");
        }
        
        BookingItem bookingItem = getBookingItem(itemId);
        if (bookingItem == null && !itemId.isEmpty()) {
            throw new BookingEngineException("Can not change to a bookingItem that does not exists");
        }
        
        String oldItemId = booking.bookingItemId;
        String oldBookingItemTypeId = booking.bookingItemTypeId;
        
        try {
            booking.bookingItemId = itemId;
            
            if (bookingItem != null)
                booking.bookingItemTypeId = bookingItem.bookingItemTypeId;

            validateBooking(booking);
            saveObject(booking);
        } catch (BookingEngineException ex) {
            booking.bookingItemId = oldItemId;
            booking.bookingItemTypeId = oldBookingItemTypeId;
            throw ex;
        }
    }

    private void unassignAllFutureBookings() {
        bookings.values().stream()
            .filter(b -> b.startsTomorrowOrLater())
            .forEach(b -> {
                if (b.bookingItemId != null && !b.bookingItemId.isEmpty()) {
                    b.prevAssignedBookingItemId = b.bookingItemId;
                    b.bookingItemId = "";
                    saveObject(b);    
                }
            });        
    }
    
    @Override
    public void forceUnassignBookingInfuture() {
        unassignAllFutureBookings();
        
        bookings.values().stream()
                .filter(b -> b.prevAssignedBookingItemId != null && !b.prevAssignedBookingItemId.isEmpty())
                .filter(b -> b.bookingItemId == null || b.bookingItemId.isEmpty())
                .forEach(booking -> {
                    String bookingId = booking.id;
                    String itemId = booking.prevAssignedBookingItemId;
                    try {
                        changeBookingItemOnBooking(bookingId, itemId);
                        logPrint("Success : " + bookingId + " , " + items.get(itemId).bookingItemName);
                    } catch (Exception ex) {
                        logPrint("Was not able to cleanup : " + bookingId + " , " + items.get(itemId).bookingItemName);
                    }
                });
    }
    
    @Override
    public void changeBookingItemAndDateOnBooking(String bookingId, String itemId, Date start, Date end) {
        Booking booking = getBooking(bookingId);
        
        if (booking == null) {
            throw new BookingEngineException("Can not change bookingitem, the booking does not exists");
        }
        
        String oldItemId = booking.bookingItemId;
        String oldBookingItemTypeId = booking.bookingItemTypeId;
        Date oldStartDate = booking.startDate;
        Date oldEndDate = booking.endDate;
        
        
        BookingItem bookingItem = getBookingItem(itemId);
        if (bookingItem == null && !itemId.isEmpty()) {
            throw new BookingEngineException("Can not change to a bookingItem that does not exists");
        }
        
        try {
            booking.bookingItemId = itemId;
            booking.startDate = start;
            booking.endDate = end;
            if (bookingItem != null) {
                booking.bookingItemTypeId = bookingItem.bookingItemTypeId;
            }
            validateBooking(booking);
            saveObject(booking);
        } catch (BookingEngineException ex) {
            ex.printStackTrace();
            booking.bookingItemId = oldItemId;
            booking.startDate = oldStartDate;
            booking.endDate = oldEndDate;
            booking.bookingItemTypeId = oldBookingItemTypeId;
            throw ex;
        }
    }

    @Override
    public void saveObject(DataCommon data) throws ErrorException {
        if (data instanceof Booking) {
            ((Booking)data).stripSeconds();
        }
        super.saveObject(data); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
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

    @Override
    public BookingTimeLineFlatten getTimeLinesForItem(Date start, Date end, String itemId) {
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
   
    @Override
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimalIngoreErrors(Date start, Date end) {
        return getTimeLinesForItemWithOptimal(start, end, false);
    }
    
    @Override
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimal(Date start, Date end, boolean ignoreErrors) {
        List<BookingTimeLineFlatten> retList = new ArrayList();        
        
        for (String bookingItemTypeId : types.keySet()) {
            BookingItemAssignerOptimal assigner = getAvailableItemsAssigner(bookingItemTypeId, start, end, null);
            assigner.disableErrorCheck();
            List<OptimalBookingTimeLine> availableBookingItems = assigner.getOptimalAssigned();
            
            assigner.getLinesOverBooked().stream()
                    .forEach(overLine -> {
                        BookingTimeLineFlatten line = new BookingTimeLineFlatten(1, bookingItemTypeId);
                        line.bookingItemId = null;
                        line.start = start;
                        line.end = end;
                        line.overFlow = true;
                        overLine.bookings.stream().forEach(b -> {
                             line.add(b);
                        });
                        retList.add(line);
                    });
            
            for (BookingItem item : items.values()) {
                if (!item.bookingItemTypeId.equals(bookingItemTypeId)) {
                    continue;
                }
                
                BookingTimeLineFlatten line = new BookingTimeLineFlatten(item.bookingSize, item.bookingItemTypeId);
                line.bookingItemId = item.id;
                line.start = start;
                line.end = end;
                
                bookings.values().stream().
                        filter(o -> o.within(start, end)).
                        filter(o -> (o.bookingItemId != null && o.bookingItemId.equals(item.id))).
                        forEach(o -> line.add(o));


                for (OptimalBookingTimeLine timeLine : availableBookingItems) {
                    if (timeLine.bookingItemId.equals(item.id)) {
                        timeLine.bookings.stream()
                        .filter(o -> !line.containsBooking(o))
                        .forEach(o -> line.add(o));
                    }
                }

                retList.add(line);
            }
        
        }
        
        return retList;
    }
    
    private BookingItemAssignerOptimal getAvailableItemsAssigner(String typeId, Date start, Date end, String bookingId) throws BookingEngineException {
        BookingItemType type = types.get(typeId);
        if (type == null) {
            throw new BookingEngineException("Can not get available items ");
        }
        
        List<Booking> bookingOfTypes = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
        
        Set<Booking> bookingsWithinDaterange = bookingOfTypes.stream()
                .filter(booking -> booking.interCepts(start, end))
                .collect(Collectors.toSet());
        
        List<Booking> checkIt = new ArrayList(bookingsWithinDaterange);
        for (Booking ibooking : checkIt) {
            List<Booking> overlapping = bookingOfTypes.stream()
                    .filter(booking -> booking.interCepts(ibooking.startDate, ibooking.endDate))
                    .collect(Collectors.toList());
            
            bookingsWithinDaterange.addAll(overlapping);
            
            for (Booking ibooking2 : overlapping) {
                List<Booking> overlapping2 = bookingOfTypes.stream()
                        .filter(booking -> booking.interCepts(ibooking2.startDate, ibooking2.endDate))
                        .collect(Collectors.toList());

                bookingsWithinDaterange.addAll(overlapping2);

            }
        }
        
        if (bookingId != null && !bookingId.isEmpty()) {
            bookingsWithinDaterange.removeIf(o -> o.id.equals(bookingId));
        }
        
        List<BookingItem> bookingItems = getBookingItemsByType(typeId);
        
        BookingItemAssignerOptimal assigner = new BookingItemAssignerOptimal(type, new ArrayList(bookingsWithinDaterange), bookingItems, shouldThrowException(), storeId);
        
        return assigner;
    }

    @Override
    public void saveRules(RegistrationRules rules) {
        config.rules = rules;
        saveObject(config);
    }

    @Override
    public List<TimeRepeaterData> getOpeningHours(String typeId) {
        List<TimeRepeaterData> result = new ArrayList();
        if(typeId == null || typeId.isEmpty()) {
            result = new ArrayList(config.openingHoursData.values());
        } else {
            BookingItemType bookingtype = getBookingItemType(typeId);
            if(bookingtype != null) {
               result = new ArrayList(bookingtype.openingHoursData.values());
            }
            
            for(TimeRepeaterData data : config.openingHoursData.values()) {
                if(data.containsCategory(typeId)) {
                    result.add(data);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void saveOpeningHours(TimeRepeaterData time, String typeId) {
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
 
    @Override
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

    @Override
    public List<BookingItem> getBookingItemsByType(String typeId) {
        return items.values().stream()
                .filter(o -> o.bookingItemTypeId != null && o.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingItem> getAvailbleItems(Date start, Date end) {
        List<BookingItem> retList = new ArrayList();
        
        types.values().stream()
                .forEach(type -> {
                    retList.addAll(getAvailbleItems(type.id, start, end));
                });
        
        return retList;
    }
    
    @Override
    public List<BookingItem> getAvailbleItems(String typeId, Date start, Date end) {
        return getAvailbleItemsWithBookingConsidered(typeId, start, end, null);
    }
    
    @Override
    public List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId) {
        
        List<BookingItem> retItems = items.values()
                .stream()
                .filter(item -> item.bookingItemTypeId.equals(typeId))
                .collect(Collectors.toList());
        
        List<String> itemIdsInUse = bookings.values()
                .stream()
                .filter(b -> b.interCepts(start, end))
                .filter(b -> !b.id.equals(bookingId))
                .filter(b -> b.bookingItemId != null && !b.bookingItemId.isEmpty())
                .map(b -> b.bookingItemId)
                .distinct()
                .collect(Collectors.toList());
        
        retItems.removeIf(item -> itemIdsInUse.contains(item.id));
        
        return retItems;        
    }
    
    @Override
    public List<Booking> getAllBookingsByBookingItem(String bookingItemId) {
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

    @Override
    public void deleteOpeningHours(String repeaterId) {
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
    
    @Override
    public void checkConsistency() {
        try {
            validateBookings(new ArrayList());
        } catch (Exception x) {
            messageManager.sendErrorNotification(x.getMessage(), x);
        }
    }

    private Boolean shouldThrowException() {
        if (getSession() == null)
            return true;
        
        if (getSession() != null && getSession().get("ignoreBookingErrors") != null && getSession().get("ignoreBookingErrors").equals("true")) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean canAdd(Booking bookingToAdd) {
        List<Booking> toCheck = new ArrayList();
        toCheck.add(bookingToAdd);
        return canAdd(toCheck);
    }
    
    @Override
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

    private List<Booking> assignAllBookingsThatHasType(String typeId) {
        List<Booking> unassignedBookings = bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(typeId))
                .filter(booking -> booking.isUnassigned())
                .collect(Collectors.toList());
        
        for (Booking booking : unassignedBookings) {
            List<BookingItem> canUseItems = getAvailbleItemsWithBookingConsidered(booking.bookingItemTypeId, booking.startDate, booking.endDate, booking.id);
            
            if (!canUseItems.isEmpty()) {
                changeBookingItemOnBooking(booking.id, canUseItems.get(0).id);
            } else {
                logPrint("Was not able to assign booking: " + booking.id + " when changing type");
            }
            
        }
        
        return unassignedBookings;
    }

    @Override
    public boolean itemInUseBetweenTime(Date start, Date end, String itemId) {
        Booking ret = bookings.values().stream()
                .filter(book -> book.interCepts(start, end))
                .filter(book -> book.bookingItemId != null && book.bookingItemId.equals(itemId))
                .findAny()
                .orElse(null);
        
        return ret != null;
    }

    @Override
    public void changeSourceOnBooking(String bookingId, String source) {
        Booking booking = getBooking(bookingId);
        booking.source = source;
        saveObject(config);
    }

    @Override
    public List<BookingItem> getAllAvailbleItemsWithBookingConsideredParalized(Date start, Date end, String bookingid) {
        List<BookingItem> res = new ArrayList();
        List<BookingItemType> types = getBookingItemTypes();
        types.stream()
                .parallel()
                .forEach(type -> {
                    long time = System.currentTimeMillis();
                    List<BookingItem> found = getAvailbleItemsWithBookingConsidered(type.id, start, end, bookingid);
                    res.addAll(found);
                    logPrint("Used for type: " + type.name + " | " + (System.currentTimeMillis() - time));            
                });
        
        return res;
    }

    @Override
    public int getNumberOfPossibleBookings(String itemType, Date start, Date end) {
        BookingTimeLineFlatten res = getTimelines(itemType, start, end);
        return res.getMaxCount();
    }

    @Override
    public boolean shouldLoadData() {
        return useNewEngine.contains(storeId);
    }

    @Override
    public List<BookingItem> getAvailbleItemsWithBookingConsideredAndShuffling(String typeId, Date start, Date end, String bookingId) {
        BookingItemAssignerOptimal assigner = getAvailableItemsAssigner(typeId, start, end, null);

        List<BookingItem> retList = assigner.getAvailableItems(bookingId, start, end).stream()
                .map(o -> items.get(o))
                .collect(Collectors.toList());
        
        List<BookingItem> retList2 = new ArrayList(retList);

        return retList2;
    }

    @Override
    public Booking getActiveBookingOnBookingItem(String bookingItemId) {
        if (bookingItemId == null || bookingItemId.isEmpty()) {
            return null;
        }
        
        return bookings.values().stream()
                .filter(b -> b.bookingItemId != null && b.bookingItemId.equals(bookingItemId))
                .filter(b -> b.isCurrentlyActive())
                .findFirst()
                .orElse(null);
    }

    @Override
    public void changeDepartmentOnType(String bookingItemTypeId, String departmentId) {
        BookingItemType type = getBookingItemType(bookingItemTypeId);
        Department department = departmentManager.getDepartment(departmentId);
        
        if (department != null) {
            type.departmentId = departmentId;
            saveObject(type);
        }
    }

    @Override
    public Integer getNumberOfAvailableExcludeClose(String bookingItemTypeId, Date start, Date end) {
        int totalAvailble = getTotalSpotsForBookingItemType(bookingItemTypeId);
        BookingTimeLineFlatten timeline = new BookingTimeLineFlatten(totalAvailble, bookingItemTypeId);
        
        bookings.values().stream()
                .filter(booking -> booking.bookingItemTypeId.equals(bookingItemTypeId))
                .filter(booking -> booking.interCepts(start, end))
                .filter(booking -> booking.source.isEmpty())
                .forEach(o -> timeline.add(o));
        
        timeline.start = start;
        timeline.end = end;
        
        int higest = 9999;
        List<BookingTimeLine> timeLines = timeline.getTimelines();
        
        if (timeLines.isEmpty()) {
            return getTotalSpotsForBookingItemType(bookingItemTypeId);
        }
        
        for(BookingTimeLine line : timeLines) {
            if(line.getAvailableSpots() < higest) {
                higest = line.getAvailableSpots();
            }
        }
        return higest;
    }
}