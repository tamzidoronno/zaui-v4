
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
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import com.thundashop.core.storemanager.StoreManager;
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

    public static List<String> useNewEngine = new ArrayList();

    public BookingEngine() {
        useNewEngine.add("3b647c76-9b41-4c2a-80db-d96212af0789");
        useNewEngine.add("a152b5bd-80b6-417b-b661-c7c522ccf305");
        useNewEngine.add("e625c003-9754-4d66-8bab-d1452f4d5562");
        useNewEngine.add("178330ad-4b1d-4b08-a63d-cca9672ac329"); // utsikten
        useNewEngine.add("a6c4029c-485e-4407-b7ad-8de3b17a951c"); // lilandtunet
        useNewEngine.add("23db81fc-2705-43ee-b141-6ab019806b7e"); // hardanger gjestehus
        useNewEngine.add("fcaa6625-17da-447e-b73f-5c07b9b7d382"); // Banken Hotell Haugesund
        useNewEngine.add("87cdfab5-db67-4716-bef8-fcd1f55b770b"); // Renahotell
        useNewEngine.add("b6949f70-5e41-4c5e-abcf-d595450f8048"); // Trysilhotell
        useNewEngine.add("32f280c2-ae25-4263-8529-624df2f01dec"); // Jegtvolden
        useNewEngine.add("7f2c47a4-7ec9-41e2-a070-1e9e8fcf4e38"); // F2 harstad
        useNewEngine.add("9dda21a8-0a72-4a8c-b827-6ba0f2e6abc0"); // Kongsvinger budget hotel
    }

    @Override
    public void initialize() throws SecurityException {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
        
        if (storeManager.getStore().isPikStore() && !useNewEngine.contains(storeId)) {
            useNewEngine.add(storeId);
        }
    }
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    public BookingEngineAbstract bookingEngineAbstract;
    
    
    @Autowired
    public BookingEngineNew bookingEngineNew;
    
    @Autowired
    public OrderManager orderManager;
    
    private IBookingEngineAbstract getBookingEngine() {
        if (useNewEngine.contains(storeId)) {
            return bookingEngineNew;
        }
        
        return bookingEngineAbstract;
    }
    
    @Override
    public BookingItemType createABookingItemType(String name) {
        return deepClone(getBookingEngine().createABookingItemType(name));
    }

    @Override
    public List<BookingItemType> getBookingItemTypes() {
        return deepClone(getBookingEngine().getBookingItemTypes());
    }

    @Override
    public List<BookingItemType> getBookingItemTypesWithSystemType(Integer systemType) {
        return deepClone(getBookingEngine().getBookingItemTypesWithSystemType(systemType));
    }

    @Override
    public BookingItemType updateBookingItemType(BookingItemType type) {
        return deepClone(getBookingEngine().updateBookingItemType(type));
    }

    @Override
    public BookingItem saveBookingItem(BookingItem item) {
        return deepClone(getBookingEngine().saveBookingItem(item));
    }

    @Override
    public BookingItem getBookingItem(String id) {
        return deepClone(getBookingEngine().getBookingItem(id));
    }

    @Override
    public List<BookingItem> getBookingItems() {
        return deepClone(getBookingEngine().getBookingItems());
    }

    @Override
    public void deleteBookingItem(String id) {
        getBookingEngine().deleteBookingItem(id);
    }

    @Override
    public void setConfirmationRequired(boolean confirmationRequired) {
        getBookingEngine().setConfirmationRequired(confirmationRequired);
    }

    @Override
    public BookingEngineConfiguration getConfig() {
        return deepClone(getBookingEngine().getConfig());
    }

    @Override
    public List<Booking> getAllBookings() {
        return deepClone(getBookingEngine().getAllBookings());
    }
    
    @Override
    public List<Booking> getAllBookingsByBookingItem(String bookingItemId) {
        return deepClone(getBookingEngine().getAllBookingsByBookingItem(bookingItemId));
    }
    
    @Override
    public List<Booking> getAllBookingsByBookingItemInDateRange(String bookingItemId, Date start, Date end) {
        List<Booking> bookings = getAllBookingsByBookingItem(bookingItemId);
        bookings.stream().filter(item -> item.interCepts(start,end)).collect(Collectors.toList());
        return bookings;
    }
    
    public void removeBookingsWhereUserHasBeenDeleted(String bookingItemId) {
        getBookingEngine().removeBookingsWhereUserHasBeenDeleted(bookingItemId);
    }

    @Override
    public void changeTypeOnBooking(String bookingId, String itemTypeId) {
        getBookingEngine().changeTypeOnBooking(bookingId, itemTypeId);
    }

    @Override
    public void changeDatesOnBooking(String bookingId, Date start, Date end) {
        getBookingEngine().changeDatesOnBooking(bookingId, start, end);
    }

    @Override
    public void changeBookingItemOnBooking(String booking, String item) {
        getBookingEngine().changeBookingItemOnBooking(booking, item);
    }

    @Override
    public void changeBookingItemAndDateOnBooking(String booking, String item, Date start, Date end) {
        getBookingEngine().changeBookingItemAndDateOnBooking(booking, item,start,end);
    }

    public boolean isConfirmationRequired() {
        return getBookingEngine().isConfirmationRequired();
    }

    public boolean canAdd(List<Booking> bookingsToAdd) {
        return getBookingEngine().canAdd(bookingsToAdd);
    }

    public BookingGroup addBookings(List<Booking> bookingsToAdd) {
        return deepClone(getBookingEngine().addBookings(bookingsToAdd));
    }

    @Override
    public Booking getBooking(String bookingId) {
        return deepClone(getBookingEngine().getBooking(bookingId));
    }

    @Override
    public boolean  deleteBooking(String id) {
        return getBookingEngine().deleteBooking(id);
    }

    @Override
    public BookingItemType getBookingItemType(String id) {
        return deepClone(getBookingEngine().getBookingItemType(id));
    }

    public void addAvailability(String bookingItemId, Availability availability) {
        getBookingEngine().addAvailability(bookingItemId, availability);
    }

    public Availability getAvailbility(String id) {
        return deepClone(getBookingEngine().getAvailbility(id));
    }

    public List<Booking> getConfirmationList(String bookingItemTypeId) {
        return deepClone(getBookingEngine().getConfirmationList(bookingItemTypeId));
    }
    
    public void confirmBooking(String bookingItemTypeId) {
        getBookingEngine().confirmBooking(bookingItemTypeId);
    }

    public BookingTimeLineFlatten getTimelines(String id, Date startDate, Date endDate) {
        return deepClone(getBookingEngine().getTimelines(id, startDate, endDate));
    }

    @Override
    public void deleteBookingItemType(String id) {
        getBookingEngine().deleteBookingItemType(id);
    }

    /**
     * @todo Make unit tests for this.
     * @param start
     * @param end
     * @param itemId
     * @return 
     */
    public BookingTimeLineFlatten getTimeLinesForItem(Date start, Date end, String itemId) {
        return deepClone(getBookingEngine().getTimeLinesForItem(start,end,itemId));
    }
    
    public List<BookingTimeLineFlatten> getTimeLinesForItemWithOptimal(Date start, Date end) {
        return deepClone(getBookingEngine().getTimeLinesForItemWithOptimal(start,end, false));
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
        return getBookingEngine().itemInUseBetweenTime(start, end, itemId);
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
        return getBookingEngine().getAvailbleItems(itemType, start, end).size();
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
        return getBookingEngine().getNumberOfAvailable(itemType, start, end);
    }

    /**
     * @TODO Make unit tests for this?
     * @return 
     */
    @Override
    public RegistrationRules getDefaultRegistrationRules() {
        return getBookingEngine().getConfig().rules;
    }

     /**
     * @TODO Make unit tests for this?
     * @return 
     */
    @Override
    public void saveDefaultRegistrationRules(RegistrationRules rules) {
        getBookingEngine().saveRules(rules);
    }

    @Override
    public void saveOpeningHours(TimeRepeaterData time, String itemId) {
        getBookingEngine().saveOpeningHours(time, itemId);
    }

    @Override
    public List<TimeRepeaterData> getOpeningHoursWithType(String itemId, Integer timePeriodeType) {
        List<TimeRepeaterData> toReturn = new ArrayList();
        List<TimeRepeaterData> openingHours = deepClone(getBookingEngine().getOpeningHours(itemId));
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
        return getBookingEngine().getBookingItemsByType(typeId);
    }

    @Override
    public List<BookingItem> getAvailbleItems(String typeId, Date start, Date end) {
        return deepClone(getBookingEngine().getAvailbleItems(typeId, start, end));
    }
    
    @Override
    public List<BookingItem> getAvailbleItemsWithBookingConsidered(String typeId, Date start, Date end, String bookingId) {
        return deepClone(getBookingEngine().getAvailbleItemsWithBookingConsidered(typeId, start, end, bookingId));
    }

    @Override
    public List<BookingItem> getAllAvailbleItems(Date start, Date end) {
        return deepClone(getBookingEngine().getAvailbleItems(start, end));
    }

    @Override
    public List<BookingItem> getAllAvailbleItemsWithBookingConsidered(Date start, Date end, String bookingid) {
        if (storeId.equals("178330ad-4b1d-4b08-a63d-cca9672ac329")) {
            List<BookingItem> res = getBookingEngine().getAllAvailbleItemsWithBookingConsideredParalized(start, end, bookingid);
            return deepClone(res);
        }
        
        List<BookingItem> res = new ArrayList();
        List<BookingItemType> types = getBookingItemTypesWithSystemType(null);
        for(BookingItemType type : types) {
            List<BookingItem> found = getAvailbleItemsWithBookingConsidered(type.id, start, end, bookingid);
            res.addAll(found);
        }
        
        return deepClone(res);
    }

    @Override
    public void deleteOpeningHours(String repeaterId) {
        getBookingEngine().deleteOpeningHours(repeaterId);
    }

    @Override
    public void checkConsistency() {
        getBookingEngine().checkConsistency();
    }

    public boolean canAdd(Booking bookingToAdd) {
        return getBookingEngine().canAdd(bookingToAdd);
    }

    public BookingItem getBookingItemUnfinalized(String id) {
        return deepClone(getBookingEngine().getBookingItemUnfinalized(id));
    }

    @Override
    public void changeBookingItemType(String itemId, String newTypeId) {
        getBookingEngine().changeBookingItemType(itemId, newTypeId);
    }

    @Override
    public void changeSourceOnBooking(String bookingId, String source) {
        getBookingEngine().changeSourceOnBooking(bookingId, source);
    }

    @Override
    public void forceUnassignBookingInfuture() {
        getBookingEngine().forceUnassignBookingInfuture();
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
        return deepClone(getBookingEngine().getTimeLinesForItemWithOptimalIngoreErrors(start,end));
    }

    @Override
    public boolean canAddBooking(Booking bookingsToAdd) {
        return canAdd(bookingsToAdd);
    }
    
    @Override
    public boolean canAddBookings(List<Booking> bookingsToAdd) {
        return canAdd(bookingsToAdd);
    }

    @Override
    public List<BookingItem> getAvailbleItemsWithBookingConsideredAndShuffling(String typeId, Date start, Date end, String bookingId) {
        return deepClone(getBookingEngine().getAvailbleItemsWithBookingConsideredAndShuffling(typeId, start, end, bookingId));
    }

    public Booking getActiveBookingOnBookingItem(String bookingItemId) {
        return getBookingEngine().getActiveBookingOnBookingItem(bookingItemId);
    }

    @Override
    public void changeDepartmentOnType(String bookingItemTypeId, String departmentId) {
        getBookingEngine().changeDepartmentOnType(bookingItemTypeId, departmentId);
        BookingItemType type = getBookingItemType(bookingItemTypeId);
        
        if (type != null && type.departmentId != null && !type.departmentId.isEmpty()) {
            updateAllOrdersToUseNewDepartment(bookingItemTypeId, departmentId);
        }
    }

    private void updateAllOrdersToUseNewDepartment(String bookingItemTypeId, String departmentId) {
        List<Order> allOrders = orderManager.getAllOrders();
        BookingItemType type = getBookingItemType(bookingItemTypeId);
        
        for (Order order : allOrders) {
            boolean anyUpdated = false;
            
            for (CartItem item : order.getCartItems()) {
                if (item.departmentId == null || !item.departmentId.isEmpty()) {
                    continue;
                }
                    
                if (item.getProduct() != null && item.getProduct().id.equals(type.productId)) {
                    item.departmentId = type.departmentId;
                    anyUpdated = true;
                }
            }
            
            if (anyUpdated) {
                orderManager.saveObject(order);
            }
        }
    }
}
