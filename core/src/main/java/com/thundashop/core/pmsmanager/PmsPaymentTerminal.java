
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.printmanager.PrintJob;
import com.thundashop.core.printmanager.PrintManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsPaymentTerminal extends GetShopSessionBeanNamed implements IPmsPaymentTerminal {

    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    StoreApplicationPool storeApplicationPool;
    
    @Autowired
    PrintManager printManager;
    
    @Override
    public List<PaymentTerminalSearchResult> findBookings(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.length() < 5) {
            return new ArrayList();
        }
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.searchWord = phoneNumber;
        List<PmsBooking> bookings = pmsManager.getAllBookings(filter);
        
        List<PaymentTerminalSearchResult> result = new ArrayList();
        for(PmsBooking booking : bookings) {
            if(booking.isEnded()) {
                continue;
            }
            if(hasPhoneNumber(booking, phoneNumber)) {
                pmsManager.finalize(booking);
                User user = userManager.getUserById(booking.userId);
                PaymentTerminalSearchResult searchResult = new PaymentTerminalSearchResult();
                searchResult.id = booking.id;
                searchResult.name = user.fullName;
                searchResult.numberOfRooms = booking.getActiveRooms().size();
                searchResult.bookingAmount = booking.getTotalPrice();
                result.add(searchResult);
            }
        }
        return result;
    }

    private boolean hasPhoneNumber(PmsBooking booking,String phoneNumber) {
        User user = userManager.getUserById(booking.userId);

        if(user.cellPhone != null && user.cellPhone.equals(phoneNumber)) {
            return true;
        } 

        for(PmsBookingRooms room : booking.rooms) {
            for(PmsGuests guest : room.guests) {
                if(guest.phone != null && guest.phone.equals(phoneNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addProductToRoom(String productId, String pmsBookingRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room =  booking.getRoom(pmsBookingRoomId);
        
        PmsBookingAddonItem pmsAddon = pmsManager.getAddonsWithDiscountForBooking(pmsBookingRoomId)
                .stream()
                .filter(o -> o.productId.equals(productId))
                .findAny()
                .orElse(null);
        
        if(pmsAddon.dependsOnGuestCount) {
            pmsAddon.count = room.numberOfGuests;
        } else {
            pmsAddon.count = 1;
        }
        pmsManager.addAddonOnRoom(pmsBookingRoomId, pmsAddon);
 
        NewOrderFilter bookingFilter = new NewOrderFilter();
        pmsInvoiceManager.clearOrdersOnBooking(booking);
        pmsInvoiceManager.createOrder(booking.id, bookingFilter);        
    }
    
    @Override
    public void removeProductFromRoom(String productId, String pmsBookingRoomId) {
        pmsManager.removeProductFromRoom(pmsBookingRoomId, productId);
        
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        NewOrderFilter bookingFilter = new NewOrderFilter();
        pmsInvoiceManager.clearOrdersOnBooking(booking);
        pmsInvoiceManager.createOrder(booking.id, bookingFilter);
    }
    
    @Override
    public List<PmsOrderSummary> getOrderSummary(String bookingId) {
        List<PmsOrderSummary> res = new ArrayList();
        PmsBooking booking = pmsManager.getBooking(bookingId);
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            PmsOrderSummary summary = new PmsOrderSummary();
            summary.amount = orderManager.getTotalAmount(order);
            summary.incrementOrderId = order.incrementOrderId;
            summary.id = order.id;
            summary.paid = (order.status == Order.Status.PAYMENT_COMPLETED);
            res.add(summary);
        }
        return res;
    }

    @Override
    public PmsBooking startBooking(PmsStartBooking data) {
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        
        PmsBooking booking = pmsManager.startBooking();
        booking.couponCode = data.discountCode;
        for(Integer i = 0; i < data.numberOfRooms; i++) {
            Integer guestCount = data.guestPerRoom.get(i);

            PmsBookingRooms room = getRoomFromDataObject(data, guestCount);
            BookingItemType type = getMostSuitableType(types, room);
            
            if(type != null) {
                room.bookingItemTypeId = type.id;
                room.maxNumberOfGuests = type.size;
                booking.rooms.add(room);
                pmsManager.resetPriceForRoom(room.pmsBookingRoomId);
            }
        }
        
        pmsManager.finalize(booking);
        
        return booking;
    }

    private BookingItemType getMostSuitableType(List<BookingItemType> types, PmsBookingRooms room) {
        BookingItemType mostSuitable = null;
        for(BookingItemType type : types) {
            if(!type.visibleForBooking) {
                continue;
            }
           if(type.size >= room.numberOfGuests && (mostSuitable == null || mostSuitable.size > type.size)) {
               Booking booking = new Booking();
               booking.bookingItemTypeId = type.id;
               booking.startDate = room.date.start;
               booking.endDate = room.date.end;
               if(bookingEngine.canAdd(booking)) {
                   mostSuitable = type;
               }
           }
        }
        
        return mostSuitable;
    }

    @Override
    public boolean updateBooking(PmsBooking booking, User user, Company company) {
        if(booking.isCompletedBooking()) {
            pmsManager.saveBooking(booking);
            userManager.saveUser(user);
            return true;
        } else {
            if(company != null) {
                booking.registrationData.resultAdded.put("choosetyperadio", "company");
                booking.registrationData.resultAdded.put("company_name", company.name);
                booking.registrationData.resultAdded.put("company_vatNumber", company.vatNumber);
                booking.registrationData.resultAdded.put("company_contactPerson", company.contactPerson);
                booking.registrationData.resultAdded.put("company_phone", company.phone);
                booking.registrationData.resultAdded.put("company_email", company.email);
                if(company.address != null) {
                    booking.registrationData.resultAdded.put("company_address_address", company.address.address);
                    booking.registrationData.resultAdded.put("company_address_city", company.address.city);
                    booking.registrationData.resultAdded.put("company_address_postCode", company.address.postCode);
                }
            } else {
                booking.registrationData.resultAdded.put("user_fullName", user.fullName);
                booking.registrationData.resultAdded.put("user_emailAddress", user.emailAddress);
                booking.registrationData.resultAdded.put("user_prefix", user.prefix);
                booking.registrationData.resultAdded.put("user_cellPhone", user.cellPhone);
                if(user.address != null) {
                    booking.registrationData.resultAdded.put("user_address_address", user.address.address);
                    booking.registrationData.resultAdded.put("user_address_city", user.address.city); 
                    booking.registrationData.resultAdded.put("user_address_postCode", user.address.address);
                    booking.registrationData.resultAdded.put("user_address_countrycode", user.address.countrycode);
                }
            }
            booking.channel = "terminal";
            try {
                pmsManager.setBooking(booking);
            }catch(Exception e) {
                e.printStackTrace();
                return false;
            }
            PmsBooking res = pmsManager.completeCurrentBooking();
           if(res != null) {
               return true;
           }
        }
        
        return false;
    }

    @Override
    public HashMap<Integer, Integer> getMaxNumberOfRooms(PmsStartBooking data) {
        PmsBookingRooms room = getRoomFromDataObject(data, 0);
        LinkedHashMap<String, BookingItemType> typesMap = new LinkedHashMap();
        
        Map<BookingItemType, Integer> rollCall = new TreeMap<BookingItemType, Integer>(
                new Comparator<BookingItemType>() {
                @Override
                public int compare(BookingItemType p1, BookingItemType p2) {
                    return p2.size.compareTo(p1.size);
                }
            }
        );


        Integer count = 0;
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            typesMap.put(type.id, type);
        }
        
        HashMap<Integer, Integer> availableMap = new HashMap();
        int i = 0;
        for(BookingItemType type : bookingEngine.getBookingItemTypes()) {
            if(!type.visibleForBooking) {
                continue;
            }
            List<BookingItem> available = bookingEngine.getAvailbleItems(type.id, room.date.start, room.date.end);
            for(BookingItem item : available) {
                availableMap.put(i, typesMap.get(item.bookingItemTypeId).size);
                i++;
            }
        }
        
        return availableMap;
    }


    private PmsBookingRooms getRoomFromDataObject(PmsStartBooking data, Integer guestCount) {
        PmsBookingRooms room = new PmsBookingRooms();
        room.numberOfGuests = guestCount;
        room.date.start = new Date();

        if(data.start != null) {
            room.date.start = data.start;
        } else {
            PmsBookingDateRange defaultObject = pmsManager.getDefaultDateRange();
            room.date.start = defaultObject.start;
        }


        room.date.end = new Date();
        if(data.end != null) {
            room.date.end = data.end;
        } else {
            PmsBookingDateRange defaultObject = pmsManager.getDefaultDateRange();
            room.date.end = defaultObject.end;
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.end);
            cal.add(Calendar.DAY_OF_YEAR, (data.numberOfNights-1));
            room.date.end = cal.getTime();
        }
        return room;
    }

    @Override
    public HashMap<String, Integer> getRoomTypesThatRoomCanBeChangedTo(String pmsBookingRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.findRoom(pmsBookingRoomId);
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        HashMap<String, Integer> availability = new HashMap();
        for(BookingItemType type : types) {
            if(!type.visibleForBooking) {
                continue;
            }
            Integer count = bookingEngine.getNumberOfAvailable(type.id, room.date.start, room.date.end);
            availability.put(type.id, count);
        }
        
        List<String> remove = new ArrayList();
        for(PmsBookingRooms tmpRoom : booking.getActiveRooms()) {
            Integer count = 0;
            if(availability.get(tmpRoom.bookingItemTypeId) != null) {
                count = availability.get(tmpRoom.bookingItemTypeId);
            }
            count--;
            availability.put(tmpRoom.bookingItemTypeId, count);
            if(count <= 0) {
                remove.add(tmpRoom.bookingItemTypeId);
            }
        }
        
        for(String removeId : remove) {
            availability.remove(removeId);
        }
        
        return availability;
        
    }

    @Override
    public PmsBookingRooms changeRoomTypeOnRoom(String pmsBookingRoomId, String newTypeId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.findRoom(pmsBookingRoomId);
        if(booking.isCompletedBooking()) {
            pmsManager.setNewRoomType(pmsBookingRoomId, booking.id, newTypeId);
        } else {
            room.bookingItemTypeId = newTypeId;
            pmsManager.saveBooking(booking);
        }
        pmsManager.finalize(booking);
        room.maxNumberOfGuests = bookingEngine.getBookingItemType(room.bookingItemTypeId).size;
        return room;
    }

    @Override
    public Double changeGuestCountOnRoom(String pmsBookingRoomId, Integer count) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.findRoom(pmsBookingRoomId);
        room.numberOfGuests = count;
        pmsManager.resetPriceForRoom(pmsBookingRoomId);
        pmsManager.finalize(booking);
        booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        pmsManager.finalize(booking);
        room = booking.findRoom(pmsBookingRoomId);
        return room.totalCost;
    }

    @Override
    public String payIndividualRoom(String pmsBookingRoomId) {
        PmsBooking booking = pmsManager.getBookingFromRoom(pmsBookingRoomId);
        pmsInvoiceManager.clearOrdersOnBooking(booking);
        
        NewOrderFilter filter = new NewOrderFilter();
        filter.pmsRoomId = pmsBookingRoomId;
        String orderId = pmsInvoiceManager.createOrder(booking.id, filter);
        
        filter.pmsRoomId = null;
        filter.createNewOrder = true;
        pmsInvoiceManager.createOrder(booking.id, filter);
        
        return orderId;
    }

    @Override
    public PmsBooking getBooking(String bookingId) {
        PmsBooking booking = pmsManager.getBooking(bookingId);
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            room.paidFor = pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId);
        }
        return booking;
    }

    @Override
    public void printReciept(String orderId) {
        Order order = orderManager.getOrder(orderId);
        User user = userManager.getUserById(order.userId);
        String text = order.createThermalPrinterReciept(getAccountingDetails(), user);
        PrintJob job = new PrintJob();
        job.content = text;
        job.createdDate = new Date();
        job.printerId = "test";
        
        printManager.addPrintJob(job);
    }
    
    private AccountingDetails getAccountingDetails() throws ErrorException {
        Application settings = storeApplicationPool.getApplicationIgnoreActive("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        AccountingDetails details = new AccountingDetails();
        if(settings != null) {
            details.accountNumber = settings.getSetting("accountNumber");
            details.iban = settings.getSetting("iban");
            details.swift = settings.getSetting("swift");
            details.address = settings.getSetting("address");
            details.city = settings.getSetting("city");
            details.companyName = settings.getSetting("companyName");
            details.contactEmail = settings.getSetting("contactEmail");
            details.dueDays = Integer.parseInt(settings.getSetting("duedays"));
            details.vatNumber = settings.getSetting("vatNumber");
            details.webAddress = settings.getSetting("webAddress");
            details.useLanguage = settings.getSetting("language");
            String kidSize = settings.getSetting("kidSize");
            if(kidSize != null && !kidSize.isEmpty()) {
                details.kidSize = new Integer(kidSize);
            }
            details.kidType = settings.getSetting("defaultKidMethod");
            details.type = settings.getSetting("type");
            details.currency = orderManager.getLocalCurrencyCode();
        }

        return details;
    }

}
